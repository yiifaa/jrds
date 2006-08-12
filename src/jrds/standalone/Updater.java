package jrds.standalone;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import jrds.DescFactory;
import jrds.HostsList;
import jrds.Probe;
import jrds.PropertiesManager;
import jrds.RdsHost;
import jrds.StoreOpener;

import org.apache.log4j.Logger;
import org.jrobin.core.RrdDb;
import org.jrobin.core.RrdDef;
import org.jrobin.core.RrdException;

public class Updater {
	static {
		jrds.log.JrdsLoggerFactory.initLog4J();
	}
	static final private Logger logger = Logger.getLogger(Updater.class);
	public static final int GRAPH_RESOLUTION = 300; // seconds
	private static final PropertiesManager pm = PropertiesManager.getInstance();

	public static void main(String[] args) {
		pm.join(new File("jrds.properties"));
		pm.update();
		//jrds.log.JrdsLoggerFactory.setOutputFile(pm.logfile);

		System.getProperties().setProperty("java.awt.headless","true");
		System.getProperties().putAll(pm.getProperties());
		try {
			StoreOpener.prepare(pm.dbPoolSize, pm.syncPeriod);
		} catch (RrdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DescFactory.init();
		final HostsList hl = HostsList.getRootGroup();
		//DescFactory.scanProbeDir(new File("config"));
		if(pm.configdir != null)
			DescFactory.scanProbeDir(new File(pm.configdir, "macro"));
		if(pm.configdir != null)
			DescFactory.scanProbeDir(new File(pm.configdir));

		ExecutorService tpool =  Executors.newFixedThreadPool(3);

		for(RdsHost host: hl.getHosts()) {
			for(final Probe p: host.getProbes()) {
				final Runnable runUpgrade = new Runnable() {
					private Probe lp = p;
					
					public void run() {
						try {
							RrdDef rrdDef = lp.getRrdDef();
							File source = new File(lp.getRrdName());
							File dest = File.createTempFile("JRDS_", ".tmp", source.getParentFile());
							logger.debug("updating " +  source  + " to "  + dest);
							RrdDb rrdSource = StoreOpener.getRrd(source.getCanonicalPath());
							rrdDef.setPath(dest.getCanonicalPath());
							RrdDb rrdDest = new RrdDb(rrdDef);
							rrdSource.copyStateTo(rrdDest);
							rrdDest.close();
							StoreOpener.releaseRrd(rrdSource);
							logger.debug("Size difference : " + (dest.length() - source.length()));
							copyFile(dest.getCanonicalPath(), source.getCanonicalPath());
						} catch (RrdException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				};
				try {
					tpool.execute(runUpgrade);
				}
				catch(RejectedExecutionException ex) {
					logger.debug("collector thread dropped for probe " + p.getName());
				}


			}
		}
		tpool.shutdown();
		try {
			tpool.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.info("Collect interrupted");
		}
		StoreOpener.stop();
	}
	private static void copyFile(String sourcePath, String destPath)
	throws IOException {
		File source = new File(sourcePath);
		File dest = new File(destPath);
		deleteFile(dest);
		if (!source.renameTo(dest)) {
			throw new IOException("Could not create file " + destPath + " from " + sourcePath);
		}
	}
	private static void deleteFile(File file) throws IOException {
		if (file.exists() && !file.delete()) {
			throw new IOException("Could not delete file: " + file.getCanonicalPath());
		}
	}




}
