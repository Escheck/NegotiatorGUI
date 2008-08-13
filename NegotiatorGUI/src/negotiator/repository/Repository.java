package negotiator.repository;

import java.util.ArrayList;

public class Repository
{
		ArrayList<RepItem> items;
		
		public Repository() { 
			items=new ArrayList<RepItem>();
		}
		
		public ArrayList<RepItem> getItems() { return items; }
		
		 /** @returns AgentRepItem of given className, or null if none exists */
		public AgentRepItem getAgentOfClass(String className)
		{
			for (RepItem it: items) {
				if (it instanceof AgentRepItem)
					if (((AgentRepItem)it).classPath.equals(className))
						return (AgentRepItem) it;
			}
			return null;
		}
}