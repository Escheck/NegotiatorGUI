package negotiator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map.Entry;

import negotiator.issue.ISSUETYPE;
import negotiator.issue.Issue;
import negotiator.issue.Objective;
import negotiator.issue.ValueDiscrete;
import negotiator.repository.DomainRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.RepItem;
import negotiator.repository.Repository;
import negotiator.utility.Evaluator;
import negotiator.utility.EvaluatorDiscrete;
import negotiator.utility.AdditiveUtilitySpace;

/**
 * Class used to validate the domain, preference profile, and the consistency
 * between both.
 * 
 * @author Mark Hendrikx
 */
public class ScenarioValidator {

	private static String validateDomain(Domain domain) {
		String errors = "";
		ArrayList<Objective> objectives = domain.getObjectives();
		HashSet<String> names = new HashSet<String>();

		for (int i = 0; i < objectives.size(); i++) {
			// Check indices
			if (objectives.get(i).getNumber() != i) {
				errors += ("Index of \"" + objectives.get(i).getName()
						+ "\" is " + objectives.get(i).getNumber()
						+ " but should be " + i + "\n");
			}
			// Check unique names
			if (!names.add(objectives.get(i).getName())) {
				errors += ("There already exists an element with objective name \""
						+ objectives.get(i).getName() + "\"\n");
			}
		}
		return errors;
	}

	private static String validateCorrespondenceDomainAndProfile(Domain domain,
			AdditiveUtilitySpace space) {

		String errors = isComplete(space);
		if (errors == null) {
			errors = "";
		} else {
			errors += "\n";
		}
		return errors;
	}

	private static String isComplete(AdditiveUtilitySpace space)
	// Oh damn, problem, we don't have the domain template here anymore.
	// so how can we check domain compativility?
	// only we can check that all fields are filled.........
	{
		Domain domain = space.getDomain();
		ArrayList<Issue> issues = domain.getIssues();
		if (issues == null)
			return "Utility space is not complete, in fact it is empty!";
		String mess;
		for (Issue issue : issues) {
			Evaluator ev = space.getEvaluator(issue.getNumber());
			if (ev == null)
				return "issue " + issue.getName() + " has no evaluator";
			mess = (ev.isComplete(issue));
			if (mess != null)
				return mess;
		}
		return null;
	}

	private static String validatePreferenceProfile(AdditiveUtilitySpace space) {
		String errors = "";
		double weightSum = 0;
		for (Entry<Objective, Evaluator> pair : space.getEvaluators()) {
			Objective obj = pair.getKey();
			Evaluator eval = pair.getValue();

			if (obj == null) {
				errors += "Mismatch in objective indices between domain and preference profile\n";
				return errors;
			}

			if (obj.getType() == ISSUETYPE.DISCRETE) {
				EvaluatorDiscrete dEval = (EvaluatorDiscrete) eval;
				boolean allZero = true;
				for (ValueDiscrete dValue : dEval.getValues()) {
					try {
						double evaluation = dEval
								.getEvaluationNotNormalized(dValue);

						if (evaluation < 0) {
							errors += "Value \""
									+ dValue.getValue()
									+ "\""
									+ " of objective \""
									+ obj.getName()
									+ "\" must have a non-negative evaluation\n";
						} else if (evaluation > 0) {
							allZero = false;
						}
					} catch (Exception e) {
						errors += e.getMessage() + "\n";
					}
				}
				if (allZero) {
					errors += "Objective \""
							+ obj.getName()
							+ "\" does not have a value with a non-zero positive evaluation\n";
				}
			}
			weightSum += eval.getWeight();
		}
		if (Math.abs(1.0 - weightSum) > 0.01) {
			errors += "The sum of the issue weights differs significantly from one\n";
		}
		return errors;
	}

	public static String validateDomainRepository(Repository domainrepository) {
		String errors = "";
		try {
			for (RepItem repitem : domainrepository.getItems()) {
				DomainRepItem dri = (DomainRepItem) repitem;
				Domain domain = new Domain(dri.getURL().getFile());

				for (ProfileRepItem pri : dri.getProfiles()) {
					AdditiveUtilitySpace space = new AdditiveUtilitySpace(domain, pri.getURL()
							.getFile());
					String newErrors = "";
					newErrors += (validateDomain(domain));
					newErrors += (validateCorrespondenceDomainAndProfile(
							domain, space));
					newErrors += (validatePreferenceProfile(space));

					if (!newErrors.trim().equals("")) {
						errors += "DOMAIN: " + dri.getName() + " PROFILE: "
								+ pri.getURL().getFile() + "\n" + newErrors
								+ "\n";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return errors;
	}

	public static void main(String[] args) {
		try {
			Repository domainrepository = Repository.get_domain_repos();
			String result = validateDomainRepository(domainrepository);
			if (result.trim().equals("")) {
				System.out.println("All scenarios are OK");
			} else {
				System.out.println(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
