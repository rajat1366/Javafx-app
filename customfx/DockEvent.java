package com.fousalert.customfx;
import com.sun.javafx.scene.input.InputEventUtils;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.input.PickResult;
@SuppressWarnings("restriction")
public class DockEvent extends Event {
	private static final long serialVersionUID = 4413700316447127355L;
	
	public static final EventType<DockEvent> ANY = new EventType<DockEvent>(Event.ANY, "DOCK");
	public static final EventType<DockEvent> DOCK_ENTER = new EventType<DockEvent>(DockEvent.ANY, "DOCK_ENTER");
	public static final EventType<DockEvent> DOCK_OVER = new EventType<DockEvent>(DockEvent.ANY, "DOCK_OVER");
	public static final EventType<DockEvent> DOCK_EXIT = new EventType<DockEvent>(DockEvent.ANY, "DOCK_EXIT");
	public static final EventType<DockEvent> DOCK_RELEASED = new EventType<DockEvent>(DockEvent.ANY, "DOCK_RELEASED");
	
	private DockPane parentPane;
	private transient double x;
	private transient double y;
	private transient double z;
	private final double screenX;
	private final double screenY;
	private final double sceneX;
	private final double sceneY;
	private PickResult pickResult;
	private Node contents;
	
	public DockPane getParentPane() {
		return parentPane;
	}
	public void setParentPane(DockPane parentPane) {
		this.parentPane = parentPane;
	}

	public final double getX() {
		return x;
	}
	
	public final double getY() {
		return y;
	}

	public final double getZ() {
		return z;
	}
	
	public final double getScreenX() {
		return screenX;
	}
	
	public final double getScreenY() {
		return screenY;
	}
	public final double getSceneX() {
		return sceneX;
	}
	public final double getSceneY() {
		return sceneY;
	}

	public final PickResult getPickResult() {
		return pickResult;
	}

	public final Node getContents() {
		return contents;
	}
	public DockEvent(EventType<? extends DockEvent> eventType, double x, double y, double screenX, double screenY, PickResult pickResult) {
		this(null, null, eventType, x, y, screenX, screenY, pickResult);
	}
	public DockEvent(Object source, EventTarget target, EventType<? extends DockEvent> eventType, double x, double y, double screenX, double screenY, PickResult pickResult) {
		this(source, target, eventType, x, y, screenX, screenY, pickResult, null, null);
	}
	public DockEvent(Object source, EventTarget target, EventType<? extends DockEvent> eventType,
			double x, double y, double screenX, double screenY, PickResult pickResult, Node contents, DockPane parentPane) {
		super(source, target, eventType);
		this.x = x;
		this.y = y;
		this.screenX = screenX;
		this.screenY = screenY;
		this.sceneX = x;
		this.sceneY = y;
		this.pickResult = pickResult != null ? pickResult : new PickResult(target, x, y);
		final Point3D p = InputEventUtils.recomputeCoordinates(this.pickResult, null);
		this.x = p.getX();
		this.y = p.getY();
		this.z = p.getZ();
		this.contents = contents;
		this.parentPane = parentPane;
	}
}