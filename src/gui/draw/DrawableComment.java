package gui.draw;

import gui.control.DrawController;
import gui.control.Movable;
import gui.control.Resizable;
import gui.control.Selectable;

import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import model.graph.GraphComment;
import model.graph.GraphEdge;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.svg.SVGOMTextElement;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import util.Vector;
import action.Configurable;

public class DrawableComment extends DrawableElement implements Drawable, Selectable, Movable, Resizable, Configurable {
	private int width = 50, height = 0, minimalWidth = 0;
	private Point position, resizePoint;
	private Rectangle left, right;
	private int offsetX, offsetY;
	private int resizeOffsetX, resizeWidth;
	private static final int borderSize = 7, anchorRectSize = 8;
	private Element element, frame, textGroup;
	private Element anchorRectLeft, anchorRectRight;
	private Element[] textElements;
	private String[] paragraphs;
	private boolean selected;
	private CommentType commentType;
	private GraphComment graphComment;
	
	
	private static final String TEXT_FAMILY = "Arial"; // Family of the text
	private static final String TEXT_WEIGHT = "bold"; // Weight of the text
	private static final int FONT_WEIGHT = Font.BOLD; // Weight of the font element
	private static final String TEXT_COLOR = "black"; // Color of the text
	
	private static final int FONT_SIZE_COMMENT = 14;
	private static final int FONT_SIZE_HEADLINE = 28;
	
	public static final String POSITION_X = "x";
	public static final String POSITION_Y = "y";
	public static final String POSITION = "pos";
	
	
	public static final String TEXT = "text";
	
	public static enum ResizeType{
		LEFT,
		RIGHT,
		NONE;
	}
	
	public static enum CommentType{
		COMMENT(FONT_SIZE_COMMENT, "comment", "New comment"),
		HEADLINE(FONT_SIZE_HEADLINE, "headline", "New title");
		
		private int fontSize;
		private String name;
		public final String defaultContent;

		private CommentType(int fontSize, String name, String defaultContent){
			this.fontSize = fontSize;
			this.name = name;
			this.defaultContent = defaultContent;
		}
		
		public static CommentType getType(String type){
			if(type.equals(COMMENT.name)){
				return COMMENT;
			}else if (type.equals(HEADLINE.name)){
				return HEADLINE;
			}else System.err.println("No valid commentType");
			return null;
		}
		
		public String getName(){
			return name;
		}
	}
	
	public DrawableComment(DrawableComment draft){
		super(draft.doc);
		position = DrawableGrid.getPoint(draft.position,true);
		this.paragraphs = draft.paragraphs;
		this.commentType = draft.commentType;
		selected = false;
		this.width = draft.width;
		this.minimalWidth = draft.minimalWidth;
		graphComment = new GraphComment(this, draft.getGraphComment().getEdge());
		buildElement();
	}
	
	public DrawableComment(SVGDocument doc, int posX, int posY, CommentType commentType, GraphEdge edge){
		this(doc, posX, posY, commentType, new String[]{new String(commentType.defaultContent)},edge);
	}
	
	public DrawableComment(SVGDocument doc, int posX, int posY, CommentType commentType, String[] paragraphs, GraphEdge edge) {
		super(doc);
		position = DrawableGrid.getPoint(new Point(posX,posY),true);
		this.paragraphs = paragraphs;
		this.commentType = commentType;
		selected = false;
		graphComment = new GraphComment(this,edge);
		this.graphComment.setDrawable(this);
		setWidths();
		buildElement();
	}
	
	public DrawableComment(SVGDocument doc, int posX, int posY, int width, CommentType commentType, String[] paragraphs, GraphEdge edge){
		super(doc);
		position = DrawableGrid.getPoint(new Point(posX,posY),true);
		this.paragraphs = paragraphs;
		this.commentType = commentType;
		selected = false;
		graphComment = new GraphComment(this,edge);
		this.graphComment.setDrawable(this);
		minimalWidth = this.calculateMinimalWidth();
		this.width = width;
		buildElement();
	}
	
	/**
	 * This function sets the width and the minimal width. The function is used at the initialization
	 * and if the paragraphs changes.
	 */
	private void setWidths(){
		minimalWidth = this.calculateMinimalWidth();
		width = this.calculateWidth();
	}
	
	protected void buildElement() {
		element = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "g");
		frame = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "rect");
		anchorRectLeft = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "rect");
		anchorRectRight = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "rect");
		textGroup = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "g");
		element.appendChild(frame);
		element.appendChild(textGroup);
		invalidate();
	}

	@Override
	public Element getElement() {
		return element;
	}

	@Override
	public void invalidate() {
		element.setAttribute("transform", "translate(" + (int) position.getX() + ", " + (int) position.getY() + ")");
		element.setAttribute("render-order", String.valueOf(-1));
		setText(width);
		frame.setAttribute("x", String.valueOf(0));
		frame.setAttribute("y", String.valueOf(0));
		frame.setAttribute("width", String.valueOf(width));
		frame.setAttribute("height", String.valueOf(height));
		frame.setAttribute("fill","white");
		if(!selected){
			if(anchorRectLeft.getParentNode() != null && anchorRectRight.getParentNode() != null){
				element.removeChild(anchorRectLeft);
				element.removeChild(anchorRectRight);
				frame.setAttribute("stroke", "none");
			}
		}else{
			left = new Rectangle(0 - anchorRectSize/2, height/2 - anchorRectSize/2, anchorRectSize, anchorRectSize);
			
			anchorRectLeft.setAttribute("x", String.valueOf((int) left.x));
		 	anchorRectLeft.setAttribute("y", String.valueOf((int) left.y));
			
			anchorRectLeft.setAttribute("width", String.valueOf(left.width));
			anchorRectLeft.setAttribute("height", String.valueOf(left.height));
			
			right = new Rectangle(width - anchorRectSize/2, height/2 - anchorRectSize/2, anchorRectSize, anchorRectSize);
			
			anchorRectRight.setAttribute("x", String.valueOf((int) right.x));
			anchorRectRight.setAttribute("y", String.valueOf((int) right.y));
			
			anchorRectRight.setAttribute("width", String.valueOf(right.width));
			anchorRectRight.setAttribute("height", String.valueOf(right.height));
			
			element.appendChild(anchorRectLeft);
			element.appendChild(anchorRectRight);
			frame.setAttribute("stroke", "black");
		}
	}
	/**
	 * Calculates the text length from a given string.
	 * @param s - given String
	 * @param withBorder - calculateWidth with border
	 * @return length of s
	 */
	private synchronized int getTextLength(String s, boolean withBorder){
		if (s.equals(""))return 0;
		Font defaultFont = new Font(TEXT_FAMILY, FONT_WEIGHT, this.commentType.fontSize);
		AffineTransform affinetransform = new AffineTransform();     
		FontRenderContext frc = new FontRenderContext(affinetransform,true,true);
		int result = (int) defaultFont.getStringBounds(s, frc).getWidth();
		if(withBorder){
			result += 2*borderSize;
		}
		return result;
	}
	
	/**
	 * This function is used to determine the greatest word in the text. This is the minimal width.
	 * @return minimal width of comment
	 */
	private int calculateMinimalWidth (){
		int max = 0;
		for(String s : paragraphs){
			String sl[] = s.split(" ");
			for (String word : sl){
				int temp = getTextLength(word, true);
				if (temp > max){
					max = temp;
				}
			}
		}
		return max;
	}

	/**
	 * This function is used to determine the width from a given row
	 * @param index - index from column in paragraphs
	 * @return width from column
	 */
	private int calculateWidthFromRow(int index){
		return getTextLength(paragraphs[index], true);
	}
	
	/**
	 * Calculate the start width from all rows.
	 * @return minimal width at begin
	 */
	private int calculateWidth(){
		int max = 0;
		for(int i = 0; i < paragraphs.length; i++){
			int tmp = calculateWidthFromRow(i);
			if(tmp > max){
				max = tmp;
			}
		}
		return max;
	}
		
	private void setText(int w){
		element.removeChild(textGroup);
		textGroup = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "g");
		textGroup.setAttribute("x", String.valueOf(0));
		textGroup.setAttribute("y", String.valueOf(0));
		ArrayList<String> lines = new ArrayList<String>();
		for(int i = 0; i < paragraphs.length; i++){
			String row = paragraphs[i];
			int rowWidth = this.calculateWidthFromRow(i);
			if (w < rowWidth){
				ArrayList<String> tmpList = new ArrayList<String>();
				tmpList.add("");
				String list[] = fitToWidth(row.split(" "), tmpList, 0, w);
				for (String s : list){
					lines.add(s);
				}
			}else{
				lines.add(paragraphs[i]);
			}
		}
		visualizeText(lines);
		setAnchorPointPosition();
	}
	
	private void visualizeText(ArrayList<String> list) {
		textElements = new Element[list.size()];
		int y = borderSize;
		for(int i = 0; i < textElements.length; i++) {
			y+= this.commentType.fontSize + 5;
			textElements[i] = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "text");
			SVGOMTextElement e = (SVGOMTextElement) textElements[i];
			textElements[i].setAttribute("x", String.valueOf(borderSize));
			textElements[i].setAttribute("y", String.valueOf(y));
			textElements[i].setAttribute("font-size", String.valueOf(this.commentType.fontSize));
			textElements[i].setAttribute("font-weight", TEXT_WEIGHT);
			textElements[i].setAttribute("font-family", TEXT_FAMILY);
			textElements[i].setAttribute("fill", TEXT_COLOR);
			textElements[i].setTextContent(list.get(i));
			textGroup.appendChild(textElements[i]);
		}
		height = y+borderSize;
		frame.setAttribute("height", String.valueOf(height));
		element.appendChild(textGroup);
	}
	
	private void setAnchorPointPosition(){
		left = new Rectangle(0 - anchorRectSize/2, height/2 - anchorRectSize/2, anchorRectSize, anchorRectSize);
		right = new Rectangle(width - anchorRectSize/2, height/2 - anchorRectSize/2, anchorRectSize, anchorRectSize);
		anchorRectLeft.setAttribute("y", String.valueOf((int) left.y));
		anchorRectRight.setAttribute("y", String.valueOf((int) right.y));
	}
	
	/**
	 * This function fits recursively a given text to the width of the comment.
	 * @param row - given text as array (separated with " ")
	 * @param lines - the generated lines
	 * @param rowIndex - the current row(line) index
	 * @return returns the calculated lines
	 */
	private String[] fitToWidth(String row[], ArrayList<String> lines, int rowIndex, int width){
		if(row.length == 0){
			return lines.toArray(new String[]{});
		}
		if(width < minimalWidth){
			System.err.println("[DrawableComment] MAJOR: Width < MinimalWidth");
		}
		String line = lines.get(rowIndex);
		int lineLength = this.getTextLength(line, true);
		String newWord = row[0];
		int wordLength = this.getTextLength(newWord, false);
		if(lineLength + wordLength > width){
			rowIndex++;
			lines.add(rowIndex, "");
			return fitToWidth(row, lines, rowIndex, width);
		} else{
			line = line + " " + newWord;
			lines.set(rowIndex, line);
			
			String newRow[] = new String[row.length-1];
//			System.out.println("[DrawableComment] row.length = " + row.length);
			for(int i = 0; i < newRow.length;i++){
				newRow[i] = row[i+1];
			}
			return fitToWidth(newRow, lines, rowIndex, width);
		}
	}
	
	@Override
	public String getAttribute(String cmd) {
		switch (cmd) {
		case POSITION_X:
			return String.valueOf(position.x);
		case POSITION_Y:
			return String.valueOf(position.y);
		case "width":
			return String.valueOf(width);
		case "height":
			return String.valueOf(height);
		case TEXT :
			String result = "";
			for(String s : paragraphs){
				result += s+ "\n";
			}
			return result;
		default:
			System.err.println("Unrecognized command " + cmd);
			return null;
		}
	}
	
	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public boolean isWithin(Rectangle r) {
		return r.contains(new Point((int) position.getX()+(width/2), (int) position.getY() +(height/2)));
	}

	public ResizeType getResizeType(Point p){
		p.translate((int) -position.getX(), (int) -position.getY());
		if(left.contains(p)){
			return ResizeType.LEFT;
		}else if(right.contains(p)){
			return ResizeType.RIGHT;
		}else{
			return ResizeType.NONE;
		}
	}
	
	@Override
	public boolean contains(Point p) {
		Rectangle r = new Rectangle((int) position.getX(), (int) position.getY(), width, height);
		return r.contains(p);
	}

	@Override
	public void setSelected(boolean b) {
		selected = b;
		this.invalidate();
		if(this.getGraphComment().getEdge() != null) {
			this.getGraphComment().getEdge().getDrawable().setSelected(b, false);
		}
	}

	private void updateLocation() {
		int x = (int) position.getX() + offsetX;
		int y = (int) position.getY() + offsetY;
		Point p = DrawableGrid.getPoint(new Point(x,y),true);
		//adjust offsets:
		offsetX = p.x - position.x;
		offsetY = p.y - position.y;
		
		element.setAttribute("transform", "translate(" + p.x + ", " + p.y + ")");
	}
	
	@Override
	public void moveBy(Vector v) {
		position.translate(v.getX(), v.getY());
		position = DrawableGrid.getPoint(position,true);
		offsetX = 0;
		offsetY = 0;
		updateLocation();
	}
	
	@Override
	public void moveTo(Point p) {
		moveBy(new Vector(p.x - position.x, p.y - position.y));
	}
	
	@Override
	public void setOffset(Vector v) {
		this.offsetX = v.getX();
		this.offsetY = v.getY();
		updateLocation();
	}

	@Override
	public void resizeBy(int offset, ResizeType r) {
		resizeOffsetX = 0;
		if(r.equals(ResizeType.LEFT)){
			int w = width - offset;
			if(w < minimalWidth){
				offset = width-minimalWidth;
			}
			width = width - offset;
			
			position = new Point (position.x + offset, position.y);
		}else if(r.equals(ResizeType.RIGHT)){
			int w = width + offset;
			if (w < minimalWidth){
				width = minimalWidth;
			}else{
				width += offset;
			}
		}
		updatePosition(r);	
	}

	private void updatePosition(ResizeType r){
		if(r.equals(ResizeType.RIGHT)){
			resizeWidth = width + resizeOffsetX;
			if(resizeWidth < minimalWidth){
				resizeWidth = minimalWidth;
			}
			anchorRectRight.setAttribute("x", String.valueOf(resizeWidth-anchorRectSize/2));
			frame.setAttribute("width", String.valueOf(resizeWidth));				
		}else if (r.equals(ResizeType.LEFT)){
			int x = (int) position.getX() + resizeOffsetX;
			resizeWidth = width - resizeOffsetX;
			
			if(resizeWidth >= minimalWidth){
				resizePoint = new Point(x, (int) position.getY());
			}else{
				resizeWidth = minimalWidth;
				int dw = width - minimalWidth;
				resizePoint = new Point(position.x + dw, (int) position.getY());
			}
			anchorRectRight.setAttribute("x", String.valueOf(resizeWidth-anchorRectSize/2));
			frame.setAttribute("width", String.valueOf(resizeWidth));
			element.setAttribute("transform", "translate(" + (int) resizePoint.x + ", " + (int) resizePoint.y + ")");
		}
		setText(resizeWidth);
	}
	
	@Override
	public void setResizeOffset(Vector v, ResizeType r) {
		resizeOffsetX = v.getX();
		updatePosition(r);
	}

	public int getMinimalWidth(){
		return minimalWidth;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public Point getPosition(){
		return position;
	}

	public GraphComment getGraphComment(){
		return graphComment;
	}
	
	public CommentType getCommentType(){
		return commentType;
	}
	
	@Override
	public void setAttribute(String cmd, Object value) {
		switch(cmd) {
		case TEXT:
			String temp = (String) value;
			paragraphs = temp.split("\n");
			setWidths();
			break;
		case POSITION_X:
			this.position = new Point((int) value, position.y);
			break;
		case POSITION_Y:
			this.position = new Point(position.x, (int) value);
			break;
		case POSITION:
			this.position = (Point) value;
			break;
		default:
			System.err.println("Unrecognized command " + cmd);
			return;
		}
		invalidate();
	}

	@Override
	public void updateConfigs(DrawController dc) {
		this.update(dc);
	}

	public Rectangle getBoundingBox() {
		return new Rectangle (this.position.x, this.position.y, this.width, this.height);
	}
	
}
