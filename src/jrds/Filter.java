package jrds;

import jrds.graphe.Sum;


public abstract class Filter {
	static final Filter SUM = new Filter() {
		@Override
		public boolean acceptGraph(RdsGraph graph, String path) {
			return (graph instanceof Sum);
		}
		@Override
		public String getName() {
			return "All sums";
		}
	};
	static final Filter EVERYTHING = new Filter() {
		@Override
		public boolean acceptGraph(RdsGraph graph, String path) {
			return true;
		}
		@Override
		public String getName() {
			return "Everything";
		}
	};
	static final Filter ALLHOSTS = new Filter() {
		@Override
		public boolean acceptGraph(RdsGraph graph, String path) {
			return path.startsWith("/" + HostsList.HOSTROOT + "/");
		}
		@Override
		public String getName() {
			return "All hosts";
		}
	};
	static final Filter ALLVIEWS = new Filter() {
		@Override
		public boolean acceptGraph(RdsGraph graph, String path) {
			return path.startsWith("/" + HostsList.VIEWROOT + "/");
		}
		@Override
		public String getName() {
			return "All views";
		}
	};
	static final Filter ALLSERVICES = new Filter() {
		static final private String ROOTNAME = "/" + HostsList.VIEWROOT + "/Services";
		@Override
		public boolean acceptGraph(RdsGraph graph, String path) {
			return path.startsWith(ROOTNAME);
		}
		@Override
		public String getName() {
			return "All Services";
		}
		@Override
		public GraphTree setRoot(GraphTree gt) {
			return gt.getByPath(ROOTNAME);
		}
		
	};
	public abstract boolean acceptGraph(RdsGraph graph, String path);
	public abstract String getName();
	public GraphTree setRoot(GraphTree gt) {
		return gt;
	}
}
