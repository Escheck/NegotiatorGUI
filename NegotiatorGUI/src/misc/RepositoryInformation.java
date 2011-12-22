package misc;

import java.util.ArrayList;

import negotiator.Domain;
import negotiator.analysis.BidSpace;
import negotiator.repository.DomainRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.RepItem;
import negotiator.repository.Repository;
import negotiator.utility.UtilitySpace;

/**
 * Gets all information on the current {@link Repository} in a low-level way
 */
public class RepositoryInformation
{
	public static void main(String[] args)
	{
		Repository theDomainRepos = null;
		try
		{
			theDomainRepos = Repository.get_domain_repos();

			for (RepItem r : theDomainRepos.getItems())
			{
				DomainRepItem dri = (DomainRepItem) r;
				String driFilename = dri.getURL().getFile();
				Domain domain = theDomainRepos.getDomain(dri);

				System.out.println(r + " [" + driFilename + "], size: " + domain.getNumberOfPossibleBids());
				ArrayList<ProfileRepItem> profilesRIs = dri.getProfiles();
				for (RepItem p : profilesRIs)
				{
					ProfileRepItem pri = (ProfileRepItem) p;
					String priFilename = pri.getURL().getFile();
					UtilitySpace utilitySpace = theDomainRepos.getUtilitySpace(domain, pri);

					System.out.println("   " + pri + " [" + priFilename + "], d = " + utilitySpace.getDiscountFactor());
				}

				System.out.println("   Pairs:");

				for (RepItem p : profilesRIs)
					for (RepItem q : profilesRIs)
					{
						if (p == q)
							continue;

						ProfileRepItem pri = (ProfileRepItem) p;
						ProfileRepItem qri = (ProfileRepItem) q;
						UtilitySpace utilitySpaceP = theDomainRepos.getUtilitySpace(domain, pri);
						UtilitySpace utilitySpaceQ = theDomainRepos.getUtilitySpace(domain, qri);

						BidSpace bidSpace = new BidSpace(utilitySpaceP, utilitySpaceQ);

						System.out.println("   (" + pri + ", " + qri + "), nash = " + bidSpace.getNash());
					}
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
