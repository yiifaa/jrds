package jrds.probe;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Level;

import jrds.GraphDesc;
import jrds.GraphNode;
import jrds.Probe;
import jrds.graphe.RRDToolGraphNode;
import jrds.jrrd.RRDatabase;

public class RRDToolProbe extends Probe<String, Double> {
	File rrdpath;

	public Boolean configure(File rrdpath) {
		this.rrdpath = rrdpath;
		try {
			log(Level.TRACE, "rrd is %s", rrdpath.getCanonicalPath());
		} catch (IOException e) {
		}
		return rrdpath.canRead();
	}

	/* (non-Javadoc)
	 * @see jrds.Probe#addGraph(jrds.GraphNode)
	 */
	@Override
	public void addGraph(GraphNode node) {
		super.addGraph(new RRDToolGraphNode(this, node.getGraphDesc(), rrdpath));
	}

	/* (non-Javadoc)
	 * @see jrds.Probe#addGraph(jrds.GraphDesc)
	 */
	@Override
	public void addGraph(GraphDesc gd) {
		super.addGraph(new RRDToolGraphNode(this, gd, rrdpath));
	}

	@Override
	public Map<String, Double> getNewSampleValues() {
		return Collections.emptyMap();
	}

	@Override
	public String getSourceType() {
		return "RRDToolFile";
	}

	/* (non-Javadoc)
	 * @see jrds.Probe#collect()
	 */
	@Override
	public void collect() {
	}

	/* (non-Javadoc)
	 * @see jrds.Probe#checkStoreFile()
	 */
	@Override
	protected boolean checkStoreFile() {
		return rrdpath.canRead();
	}

	/* (non-Javadoc)
	 * @see jrds.Probe#create()
	 */
	@Override
	protected void create() throws IOException {
	}

	/* (non-Javadoc)
	 * @see jrds.Probe#getLastUpdate()
	 */
	@Override
	public Date getLastUpdate() {
		try {
			RRDatabase db = new RRDatabase(rrdpath);
			return db.getLastUpdate();
		} catch (IOException e) {
		}
		return new Date(0);
	}

}
