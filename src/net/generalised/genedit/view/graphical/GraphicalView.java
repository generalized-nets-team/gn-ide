package net.generalised.genedit.view.graphical;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.generalised.genedit.baseapp.model.BaseModel;
import net.generalised.genedit.baseapp.model.BaseObservable;
import net.generalised.genedit.baseapp.model.Document;
import net.generalised.genedit.baseapp.view.BaseView;
import net.generalised.genedit.model.GnDocument;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.GnObject;
import net.generalised.genedit.model.gn.GnObjectWithPosition;
import net.generalised.genedit.model.gn.PlaceReference;
import net.generalised.genedit.model.gn.Transition;
import net.generalised.genedit.view.GnView;
import net.generalised.genedit.view.graphical.tools.GraphicTool;
import net.generalised.genedit.view.graphical.tools.GraphicToolFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Widget;

import com.cloudgarden.resource.SWTResourceManager;

public class GraphicalView extends BaseView {

	public static class DragEvent extends net.generalised.genedit.baseapp.controller.Event {
		
		private final List<Point> dragPath;
		
		public DragEvent(List<Point> dragPath) {
			this.dragPath = Collections.unmodifiableList(new ArrayList<Point>(dragPath));
		}
		
		public List<Point> getDragPath() {
			return this.dragPath;
		}
	}
	
	private Canvas canvas;
	private int canvasWidth;
	private int canvasHeight;
	private Point canvasOrigin;
	private double zoom;

	private boolean showGrid;
	private boolean snapToGrid;

	private final GeneralizedNet gn;
	private List<GnObject> drawableObjects;

	private boolean isEditing = false;
	
	public GraphicalView(BaseView parent, GeneralizedNet gn) {
		super(parent, gn.getParent(GnDocument.class));
		this.gn = gn;
		this.drawableObjects = getAllDrawableObjects(gn);
		
		this.canvasOrigin = new Point(0, 0);
//		TODO: set values based on current GN:
//		gn has properties width and height;
//		if not, a method calculateOptimalSize(out width, out height), which finds the maximum of x-coordinates and the max of y- ones and adds some extra space 
		this.canvasWidth = 1024;
		this.canvasHeight = 768;
		this.zoom = 1.0;
		
		this.showGrid = true;
		this.snapToGrid = true;
		
		addAsObserverTo(getParent(GnView.class).getSelection());
	}

	private class ScrollBarListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent event) {
		    int hSelection = canvas.getHorizontalBar().getSelection();
		    int vSelection = canvas.getVerticalBar().getSelection();
		    int destX = -hSelection - canvasOrigin.x;
		    int destY = -vSelection - canvasOrigin.y;
		    canvas.scroll(destX, destY, 0, 0, (int)(zoom*canvasWidth), (int)(zoom*canvasHeight), false);
		    canvasOrigin.x = -hSelection;
		    canvasOrigin.y = -vSelection;
		}
	}

	private void resizeScrollbars() {
		Rectangle client = canvas.getClientArea();
		ScrollBar hBar = canvas.getHorizontalBar();
		ScrollBar vBar = canvas.getVerticalBar();
		hBar.setMaximum((int)(zoom*canvasWidth));
		vBar.setMaximum((int)(zoom*canvasHeight));
		hBar.setThumb(Math.min((int)(zoom*canvasWidth), client.width));
		vBar.setThumb(Math.min((int)(zoom*canvasHeight), client.height));
		int hPage = (int)(zoom*canvasWidth) - client.width;
		int vPage = (int)(zoom*canvasHeight) - client.height;
		int hSelection = hBar.getSelection();
		int vSelection = vBar.getSelection();
		if (hSelection >= hPage) {
			if (hPage <= 0) {
				hSelection = 0;
			}
			canvasOrigin.x = -hSelection;
		}
		if (vSelection >= vPage) {
			if (vPage <= 0) {
				vSelection = 0;
			}
			canvasOrigin.y = -vSelection;
		}
//	Taken from example:
//		Reposition label and text
//		label.setBounds(origin.x + labelBounds.x, origin.y + labelBounds.y, labelBounds.width, labelBounds.height);
//		text.setBounds(origin.x + textBounds.x, origin.y + textBounds.y, textBounds.width, textBounds.height);
//		canvas.redraw();
	}
	
	// TODO make a metamodel for graphics only, place this method there, place getBounds too 
	private List<GnObject> getAllDrawableObjects(GeneralizedNet gn) {
		List<GnObject> result = new ArrayList<GnObject>();
		
		List<Transition> transitions = gn.getTransitions();
		for (Transition t : transitions) {
			result.add(t);
			
			for (PlaceReference ref : t.getInputs()) {
				result.add(ref);
			}
			for (PlaceReference ref : t.getOutputs()) {
				result.add(ref);
			}
		}

		//kofti: ako parvo sa tokenite, isUnder 6te e dobre, no puk places 6te gi zakrivat!
		//mai kato gi obhojdame naopaki, nqma problem
		result.addAll(gn.getPlaces());
		result.addAll(gn.getTokens());
		
		return result;
	}
	
	private int getX(double x) {
		return canvasOrigin.x + (int)(zoom*x);
	}
	
	private int getY(double y) {
		return canvasOrigin.y + (int)(zoom*y);
	}

	private class GnMouseListener implements MouseListener, MouseMoveListener {

		private List<Point> dragPath;
		
		public void mouseDoubleClick(MouseEvent e) {
			// TODO Auto-generated method stub
		}

		public void mouseDown(MouseEvent e) {
			int x = (int)((e.x - canvasOrigin.x) / zoom);
			int y = (int)((e.y - canvasOrigin.y) / zoom);
			
			//if Ctrl is not pressed - deselect all
			if ((e.stateMask & SWT.CTRL) == 0) {
				getParent(GnView.class).getSelection().clear(); //TODO: use BaseView.getParent(GnViewComposite.class)
			}
			if ((e.stateMask & SWT.SHIFT) == 0) {
				int len = drawableObjects.size();
				for (int i = len - 1; i >= 0; --i) {
					GnObject gnObject = drawableObjects.get(i);
					GraphicTool tool = GraphicToolFactory.getTool(
							gn, gnObject, null, zoom, canvasOrigin);
					if (tool.isUnder(x, y)) {
						getParent(GnView.class).getSelection().add(gnObject);
						if(!gn.getParent(GnDocument.class).isInSimulationMode()) {
							isEditing = true;
							//originalPosition = new Point()...
						}
						break;
					}
				}
			}
			
			// Shift + drag will not move any selected objects!
			if (getParent(GnView.class).getSelection().getObjects().isEmpty()
					|| (e.stateMask & SWT.SHIFT) != 0) {
				dragPath = new ArrayList<Point>();
				dragPath.add(new Point(x, y));
			}
		}

		public void mouseUp(MouseEvent e) {
			//isEditing = false;
			// TODO if in editing mode and starting simulation via key kombination?
			if (dragPath != null && dragPath.size() > 1) {
				
				GraphicalView.this.dispatchEvent(new DragEvent(dragPath));
				
				dragPath = null;
			}
		}

		//kato se klikne varhu place, celiqt canvas miga - zle
		
		public void mouseMove(MouseEvent e) {
//			//TODO: disable editing of model in simulation mode everywhere!
			int x = (int)((e.x - canvasOrigin.x) / zoom);
			int y = (int)((e.y - canvasOrigin.y) / zoom);
//			if (isEditing) { // && !gn.isSimulationMode()
//				if (snapToGrid) {
//					int gridSize = gn.getVisualParameters().getGridStep();
//					x = (x / gridSize) * gridSize;
//					y = (y / gridSize) * gridSize;
//				}
//				if (Math.abs(x - prevX) + Math.abs(y - prevY) >= 1) {
//					for (Object o : getParent(GnView.class).getSelection().getObjects())
//						if (o instanceof GnObject) {
//							GnObject gnObject = (GnObject)o;
//							GraphicTool tool = GraphicToolFactory.getTool(gn, gnObject, null, zoom, canvasOrigin);
//							Rectangle rectangle = tool.getRedrawArea();
//							canvas.redraw(getX(rectangle.x), getY(rectangle.y), 
//									(int)(rectangle.width * zoom), (int)(rectangle.height * zoom),
//									true);
//							//move - tuk ve4e moje i da e stretch!
//							if (gnObject instanceof Transition) {
//								((Transition)gnObject).setVisualPosition(x, y);
//							} else if (gnObject instanceof Place) {
//								((Place)gnObject).setVisualPosition(x, y);
//							}
//							//TODO: ami arcata, koqto izliza ot mqstoto?
//							rectangle = tool.getRedrawArea();
//							canvas.redraw(getX(rectangle.x), getY(rectangle.y), 
//									(int)(rectangle.width * zoom), (int)(rectangle.height * zoom),
//									true);
//						}
//					prevX = x;
//					prevY = y;
//				}
//			} else {
				//TODO: optimize this!
				//TODO: da ne se poqvqva vednaga
				boolean changed = false;
				int len = drawableObjects.size();
				for (int i = len - 1; i >= 0; --i) {
					GnObject gnObject = drawableObjects.get(i);
					//TODO: cache these tools?
					GraphicTool tool = GraphicToolFactory.getTool(
							gn, gnObject, null, zoom, canvasOrigin);
					if (tool.isUnder(x, y)) {
						changed = true;
						canvas.setToolTipText(tool.getToolTipText());
						break;
					}
				}
				if (!changed)
					canvas.setToolTipText(null);
//			}
				
			if (dragPath != null) {
				// TODO draw temporary line, then clean it!
				dragPath.add(new Point(x, y));
			}
		}
		
	}
	
	private class GnPaintListener implements PaintListener {
		
		private GC gc;
		private List<Object> selectedItems = getParent(GnView.class).getSelection().getObjects();
		
		private void drawGnObject(GnObject gnObject) {
			GraphicTool tool = GraphicToolFactory.getTool(gn, gnObject, gc, zoom, canvasOrigin);
			
			if (selectedItems.contains(gnObject))
				tool.drawSelected();
			else tool.draw();
		}

		public void paintControl(PaintEvent evt) {
			System.out.println("canvas.paintControl, event=" + evt);

			gc = evt.gc;
			
			//po vreme na simulaciq edit da e zabranen
			
			//gc.setAntialias(SWT.ON);//are you sure? too slow!
			//TODO: on platforms with no graphics library:
			//catch the exception, turn off antialias, show warning
			//TODO: tova da ne stava pri vseki paintControl!
			//also antialiasing is slow
			
			if (showGrid && !gn.getParent(GnDocument.class).isInSimulationMode()) {
				Color defaultColor = gc.getForeground();
				gc.setForeground(SWTResourceManager.getColor(128, 128, 128));//TODO: use System color!
				//TODO: ne 4ertae dobre pri malki zooms!
				int gridStep = gn.getVisualParameters().getGridStep();
				for (int i = 0; i < canvasWidth; i += gridStep)
					for (int j = 0; j < canvasHeight; j += gridStep)
						gc.drawPoint(getX(i), getY(j));
				gc.setForeground(defaultColor);
			}
			
			gc.setLineWidth((int) (zoom + 0.5));
			
			for (GnObject o : drawableObjects)
				drawGnObject(o);
		}
	}

	@Override
	protected Widget createUIComponent(Widget parent) {
		Composite composite = new Composite((Composite) parent, SWT.NONE);
		{
			//Register as a resource user - SWTResourceManager will
			//handle the obtaining and disposing of resources
			SWTResourceManager.registerResourceUser(composite);
		}
		composite.setLayout(new FillLayout());
		{
			//TODO: why here? place this in GnViewComposite
//				coolBarView = new CoolBar(this, SWT.NONE);
//				coolBarView.setBounds(0, 0, 60, 30);
//				{
////					coolItem1 = new CoolItem(coolBarView, SWT.NONE);
////					coolItem1
////						.setMinimumSize(new org.eclipse.swt.graphics.Point(
////							24,
////							22));
////					coolItem1
////						.setPreferredSize(new org.eclipse.swt.graphics.Point(
////							24,
////							22));
////					{
////						toolBar1 = new ToolBar(coolBarView, SWT.NONE);
////						coolItem1.setControl(toolBar1);
////						{
////							toolItem1 = new ToolItem(toolBar1, SWT.NONE);
////						}
////					}
//				}
		}
		{
			canvas = new Canvas(composite, SWT.H_SCROLL | SWT.V_SCROLL);
			canvas.setBounds(new Rectangle(0, 0, canvasWidth, canvasHeight));
			/*GridData canvasLData = new GridData();
			//canvasLData.widthHint = 99;
			//canvasLData.heightHint = 64;
			canvasLData.grabExcessHorizontalSpace = true;
			canvasLData.horizontalAlignment = GridData.FILL;
			canvasLData.grabExcessVerticalSpace = true;
			canvasLData.verticalAlignment = GridData.FILL;
			canvas.setLayoutData(canvasLData);*/
			// TODO: use system colors! Window background, not SWT.COLOR_WHITE!
			canvas.setBackground(SWTResourceManager.getColor(255, 255, 255));
			ScrollBarListener scrollBarListener = new ScrollBarListener();
			canvas.getHorizontalBar().addSelectionListener(scrollBarListener);
			canvas.getVerticalBar().addSelectionListener(scrollBarListener);
			canvas.addListener(SWT.Resize, new Listener() {
					public void handleEvent(Event event) {
						resizeScrollbars();
					}
			});
			GnMouseListener mouseListener = new GnMouseListener();
			canvas.addPaintListener(new GnPaintListener());
			canvas.addMouseListener(mouseListener);
			canvas.addMouseMoveListener(mouseListener);

			if(! gn.getParent(Document.class).isReadOnly()) {
				DragSource dragSource = new DragSource(canvas, DND.DROP_MOVE | DND.DROP_COPY);
				dragSource.setTransfer(new Transfer[] {TextTransfer.getInstance()});
				dragSource.addDragListener(new DragSourceListener() {
	
					public void dragFinished(DragSourceEvent event) {
					}
	
					public void dragSetData(DragSourceEvent event) {
						// TODO Auto-generated method stub
						event.data = "something";//
					}
	
					public void dragStart(DragSourceEvent event) {
						if (getParent(GnView.class).getSelection().getObjects().isEmpty()) {
							event.doit = false;
						}
						//TODO event.image =... create an image with the place!!! zoomed!
						//TODO: hide dragged item? or at least change its color?
					}
					
				});
				
				//TODO: tova pre4i na DnD za xml file!
				DropTarget dropTarget = new DropTarget(canvas, 
						DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT);
				dropTarget.setTransfer(new Transfer[]{TextTransfer.getInstance()});
				dropTarget.addDropListener(new DropTargetListener() {
	
					private Point originalPosition = null; //TODO: is this good? a transition can be stretched, arcs are even more complicated
					
					private int prevX = -1, prevY = -1;
	
					public void dragEnter(DropTargetEvent event) {
					}
	
					public void dragLeave(DropTargetEvent event) {
					}
	
					public void dragOperationChanged(DropTargetEvent event) {
						// TODO Auto-generated method stub
						
					}
	
					public void dragOver(DropTargetEvent event) {
					
						//TODO: disable editing of model in simulation mode everywhere!
	
						Point point = canvas.toControl(event.x, event.y);
						int x = (int)((point.x - canvasOrigin.x) / zoom);
						int y = (int)((point.y - canvasOrigin.y) / zoom);
						if (isEditing) { // && !gn.isSimulationMode()
							if (snapToGrid) {
								int gridSize = gn.getVisualParameters().getGridStep();
								x = (x / gridSize) * gridSize;
								y = (y / gridSize) * gridSize;
							}
							if (Math.abs(x - prevX) + Math.abs(y - prevY) >= 1) {
								for (Object o : getParent(GnView.class).getSelection().getObjects())
									if (o instanceof GnObject) {
										GnObject gnObject = (GnObject)o;
										GraphicTool tool = GraphicToolFactory.getTool(gn, gnObject, null, zoom, canvasOrigin);
										Rectangle rectangle = tool.getRedrawArea();
										canvas.redraw(getX(rectangle.x), getY(rectangle.y), 
												(int)(rectangle.width * zoom), (int)(rectangle.height * zoom),
												true);
										//move - tuk ve4e moje i da e stretch!
										if (gnObject instanceof GnObjectWithPosition) {
											((BaseModel)gnObject).set("visualPositionX", x);
											((BaseModel)gnObject).set("visualPositionY", y);
										}
										//TODO: ami arcata, koqto izliza ot mqstoto?
										rectangle = tool.getRedrawArea();
										canvas.redraw(getX(rectangle.x), getY(rectangle.y), 
												(int)(rectangle.width * zoom), (int)(rectangle.height * zoom),
												true);
									}
								prevX = x;
								prevY = y;
							}
						}// else { //tova e za tooltip, ne tuk!
	//						//TODO: optimize this!
	//						//TODO: da ne se poqvqva vednaga
	//						boolean changed = false;
	//						for (GnObject gnObject : drawableObjects) {
	//							//TODO: cache these tools?
	//							GraphicTool tool = GraphicToolFactory.getTool(
	//									gn, gnObject, null, zoom, canvasOrigin);
	//							if (tool.isUnder(x, y)) {
	//								changed = true;
	//								canvas.setToolTipText(tool.getToolTipText());
	//								break;
	//							}
	//						}
	//						if (!changed)
	//							canvas.setToolTipText(null);
	//					}
					}
	
					public void drop(DropTargetEvent event) {
						//TODO: this should not work in simulation mode... maybe it is ok
						if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
							Point point = canvas.toControl(event.x, event.y);
							int x = (int)((point.x - canvasOrigin.x) / zoom);
							int y = (int)((point.y - canvasOrigin.y) / zoom);
							if (snapToGrid) {
								int gridSize = gn.getVisualParameters().getGridStep();
								x = (x / gridSize) * gridSize;
								y = (y / gridSize) * gridSize;
							}
							for (Object o : getParent(GnView.class).getSelection().getObjects())
								if (o instanceof GnObject) {
									GnObject gnObject = (GnObject)o;
									((BaseModel)gnObject).undoableSet("visualPositionX", x);
									((BaseModel)gnObject).undoableSet("visualPositionY", y);
								}
						}
					}
	
					public void dropAccept(DropTargetEvent event) {
					}
					
				});
			} // is read only
		}
		composite.layout();
		return composite;
	}

	public GeneralizedNet getGn() {
		return gn;
	}

	@Override
	public void syncUpdate(final BaseObservable observable, final Object arg) {
		//TODO: getCurrent() ? NullPointerException
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				//observable - can be GN or Selection
				//TODO: action based on arg
				//TODO: if the model was resized, call resizeScrollbars();
				//we can redraw only a small area, but how to determine where was the old figure?
				//for example: during simulation - places & tokens only, but caution!
				drawableObjects = getAllDrawableObjects(gn);
				canvas.redraw();
			}
		});
	}

	public int getZoom() {
		return (int)(zoom*100);
	}

	public void setZoom(int zoom) {
		if (zoom > 1000)
			this.zoom = 10.0;
		else if (zoom < 10)
			this.zoom = 0.1;
		else
			this.zoom = (double)zoom / 100;
		resizeScrollbars();
		canvas.redraw();
	}

	public boolean isShowGrid() {
		return showGrid;
	}

	public void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
		canvas.redraw();
	}

	public boolean isSnapToGrid() {
		return snapToGrid;
	}

	public void setSnapToGrid(boolean snapToGrid) {
		this.snapToGrid = snapToGrid;
	}
}
