package com.fousalert.customfx;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import com.fousalert.database.beans.UITicker;
import com.fousalert.utils.Constants;
import com.fousalert.utils.Context;
import com.sun.javafx.stage.StageHelper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.Window;


@SuppressWarnings("restriction")
public class DockTitleBar extends StackPane implements EventHandler<MouseEvent> {
	
	protected DockNode dockNode;
	protected Label label;
	protected List<UITicker> tickersList;
	protected List<String> tickerAsStringList;
	private boolean isHandlerRegistered = true;
	@SuppressWarnings("unchecked")
	public DockTitleBar(String labelText) {
		this.getStyleClass().add("DockTitleBar");
		this.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
		label = new Label(labelText);
		label.setTextFill(Paint.valueOf("#FFFFFF"));
		label.setMinSize(label.getPrefWidth(), label.getPrefHeight());;
		label.getStyleClass().add("non-chart-title-bar");
		StackPane.setAlignment(label, Pos.CENTER_LEFT);
		tickersList = (List<UITicker>) Context.getContext().get(Constants.FULL_TICKER_LIST_KEY);
		tickerAsStringList = (List<String>) Context.getContext().get(Constants.TICKER_LIST_AS_STRING);
		addEventHandlers();
		this.getChildren().addAll(label);
	}
	
	public void addEventHandlers() {
		this.addEventHandler(MouseEvent.MOUSE_PRESSED, this);
		this.addEventHandler(MouseEvent.DRAG_DETECTED, this);
		this.addEventHandler(MouseEvent.MOUSE_DRAGGED, this);
		this.addEventHandler(MouseEvent.MOUSE_RELEASED, this);	
		isHandlerRegistered = true;
	}
	
	public void removeEventHandlers() {
		this.removeEventHandler(MouseEvent.MOUSE_PRESSED, this);
		this.removeEventHandler(MouseEvent.DRAG_DETECTED, this);
		this.removeEventHandler(MouseEvent.MOUSE_DRAGGED, this);
		this.removeEventHandler(MouseEvent.MOUSE_RELEASED, this);
		isHandlerRegistered = false;
	}
	
	public boolean isHandlerRegistered() {
		return isHandlerRegistered;
	}

	public void setHandlerRegistered(boolean isHandlerRegistered) {
		this.isHandlerRegistered = isHandlerRegistered;
	}

	public final boolean isDragging() {
		return dragging;
	}
	
	public final Label getLabel() {
		return label;
	}
	
	public void setDockNode(DockNode dockNode) {
		this.dockNode = dockNode;
	}
	
	public final DockNode getDockNode() {
		return dockNode;
	}
	
	private Point2D dragStart;
	private boolean dragging = false;
	private HashMap<Window, Node> dragNodes = new HashMap<Window, Node>();

	private abstract class EventTask {
		protected int executions = 0;
		public abstract void run(Node node, Node dragNode);
		public int getExecutions() {
			return executions;
		}
		public void reset() {
			executions = 0;
		}
	}

	private void pickEventTarget(Point2D location, EventTask eventTask, Event explicit) {
		ObservableList<Stage> stages =
				FXCollections.unmodifiableObservableList(StageHelper.getStages());
		for (Stage targetStage : stages) {
			if (targetStage == this.dockNode.getStage())
				continue;
			eventTask.reset();
			Node dragNode = dragNodes.get(targetStage);
			Parent root = targetStage.getScene().getRoot();
			Stack<Parent> stack = new Stack<Parent>();
			if (root.contains(root.screenToLocal(location.getX(), location.getY()))
					&& !root.isMouseTransparent()) {
				stack.push(root);
			}
			while (!stack.isEmpty()) {
				Parent parent = stack.pop();
				boolean notFired = true;
				for (Node node : parent.getChildrenUnmodifiable()) {
					if (node.contains(node.screenToLocal(location.getX(), location.getY()))
							&& !node.isMouseTransparent()) {
						if (node instanceof Parent) {
							stack.push((Parent) node);
						} else {
							eventTask.run(node, dragNode);
						}
						notFired = false;
						break;
					}
				}
				if (notFired) {
					eventTask.run(parent, dragNode);
				}
			}

			if (explicit != null && dragNode != null && eventTask.getExecutions() < 1) {
				Event.fireEvent(dragNode, explicit.copyFor(this, dragNode));
				dragNodes.put(targetStage, null);
			}
		}
	}
	
	public static String eventType;
	
	@Override
	public void handle(MouseEvent event) {
		if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
			if (dockNode.isFloating() && event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
				dockNode.setMaximized(!dockNode.isMaximized());
			} else {
				dragStart = new Point2D(event.getX(), event.getY());
			}
			if(this.getDockNode().getLayoutNode().getNodeTitle() != null) {
				eventType = this.getDockNode().getLayoutNode().getNodeTitle().toString();
			} else {
				eventType = null;
			}
		} else if (event.getEventType() == MouseEvent.DRAG_DETECTED) {
			if (!dockNode.isFloating()) {
				if (!dockNode.isCustomTitleBar() && dockNode.isDecorated()) {
					dockNode.setFloating(true, new Point2D(0, DockTitleBar.this.getHeight()), null);
				} else {
					dockNode.setFloating(true);
				}
				DockPane dockPane = this.getDockNode().getDockPane();
				if (dockPane != null) {
					dockPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
					dockPane.addEventFilter(MouseEvent.MOUSE_RELEASED, this);
				}
			} else if (dockNode.isMaximized()) {
				double ratioX = event.getX() / this.getDockNode().getWidth();
				double ratioY = event.getY() / this.getDockNode().getHeight();
				dockNode.setMaximized(false);
				dragStart = new Point2D(ratioX * dockNode.getWidth(), ratioY * dockNode.getHeight());
			}
			dragging = true;
		} else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
			if (dockNode.isFloating() && event.getClickCount() == 2
					&& event.getButton() == MouseButton.PRIMARY) {
				event.setDragDetect(false);
				event.consume();
				return;
			}

			if (!dragging)
				return;
			
			Stage stage = dockNode.getStage();
			Insets insetsDelta = this.getDockNode().getBorderPane().getInsets();
			stage.setX(event.getScreenX() - dragStart.getX() - insetsDelta.getLeft());
			stage.setY(event.getScreenY() - dragStart.getY() - insetsDelta.getTop());

			DockEvent dockEnterEvent =
					new DockEvent(this, DockEvent.NULL_SOURCE_TARGET, DockEvent.DOCK_ENTER, event.getX(),
							event.getY(), event.getScreenX(), event.getScreenY(), null, null, dockNode.getDockPane());
			DockEvent dockOverEvent =
					new DockEvent(this, DockEvent.NULL_SOURCE_TARGET, DockEvent.DOCK_OVER, event.getX(),
							event.getY(), event.getScreenX(), event.getScreenY(), null, null, dockNode.getDockPane());
			DockEvent dockExitEvent =
					new DockEvent(this, DockEvent.NULL_SOURCE_TARGET, DockEvent.DOCK_EXIT, event.getX(),
							event.getY(), event.getScreenX(), event.getScreenY(), null, null, dockNode.getDockPane());

			EventTask eventTask = new EventTask() {
				@Override
				public void run(Node node, Node dragNode) {
					executions++;

					if (dragNode != node) {
						Event.fireEvent(node, dockEnterEvent.copyFor(DockTitleBar.this, node));
						if (dragNode != null) {
							Event.fireEvent(dragNode, dockExitEvent.copyFor(DockTitleBar.this, dragNode));
						}
						dragNodes.put(node.getScene().getWindow(), node);
					}
					Event.fireEvent(node, dockOverEvent.copyFor(DockTitleBar.this, node));
				}
			};

			this.pickEventTarget(new Point2D(event.getScreenX(), event.getScreenY()), eventTask, dockExitEvent);
		} else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
			dragging = false;
			if((this.getDockNode().getLayoutNode().getNodeTitle() == null) || ((eventType != null) && (this.getDockNode().getLayoutNode().getNodeTitle() != null) && eventType.equalsIgnoreCase(this.getDockNode().getLayoutNode().getNodeTitle().toString()))) {
				DockEvent dockReleasedEvent =
						new DockEvent(this, DockEvent.NULL_SOURCE_TARGET, DockEvent.DOCK_RELEASED, event.getX(),
								event.getY(), event.getScreenX(), event.getScreenY(), null, this.getDockNode(), dockNode.getDockPane());
				
				EventTask eventTask = new EventTask() {
					@Override
					public void run(Node node, Node dragNode) {
						executions++;
						if (dragNode != node) {
							Event.fireEvent(node, dockReleasedEvent.copyFor(DockTitleBar.this, node));
						}
						Event.fireEvent(node, dockReleasedEvent.copyFor(DockTitleBar.this, node));
					}
				};
				
				dragNodes.clear();
				this.pickEventTarget(new Point2D(event.getScreenX(), event.getScreenY()), eventTask, null);
				
				// Remove temporary event handler for bug mentioned above.
				DockPane dockPane = this.getDockNode().getDockPane();
				if (dockPane != null) {
					dockPane.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
					dockPane.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
				}
			}
		}
		event.consume();
	}
}