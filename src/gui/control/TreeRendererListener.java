package gui.control;

import gui.draw.GraphPanel;

import java.util.Queue;

import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.gvt.GVTTreeRendererListener;

public class TreeRendererListener implements GVTTreeRendererListener{
	private DrawController dc;
	private GraphPanel panel;
	private Queue<Runnable> bufferQueue;
	
	public TreeRendererListener(DrawController dc, GraphPanel panel, Queue<Runnable> bufferQueue) {
		this.dc = dc;
		this.panel = panel;
		this.bufferQueue = bufferQueue;
	}
	
	private boolean executed = false;
	@Override
	public void gvtRenderingCancelled(GVTTreeRendererEvent arg0) {
	}

	@Override
	public void gvtRenderingCompleted(GVTTreeRendererEvent arg0) {
		if(!executed) {
			dc.deploy();
			executed = true;
		}
		synchronized(bufferQueue) {
			while(!bufferQueue.isEmpty()) {
				Runnable r = bufferQueue.poll();
				panel.getUpdateManager().getUpdateRunnableQueue().invokeLater(r);
				System.out.println("[GraphInterface] executed item from buffer. Remaining items: " + bufferQueue.size());
			}
		}
	}

	@Override
	public void gvtRenderingFailed(GVTTreeRendererEvent arg0) {
	}

	@Override
	public void gvtRenderingPrepare(GVTTreeRendererEvent arg0) {
	}

	@Override
	public void gvtRenderingStarted(GVTTreeRendererEvent arg0) {
	}
}
