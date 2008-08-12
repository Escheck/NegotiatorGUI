package negotiator.repository;

import java.util.ArrayList;

public class Repository
{
		ArrayList<RepItem> items;
		
		public Repository() { 
			items=new ArrayList<RepItem>();
		}
		
		public ArrayList<RepItem> getItems() { return items; }
}