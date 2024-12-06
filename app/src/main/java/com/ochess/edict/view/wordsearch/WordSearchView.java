package com.ochess.edict.view.wordsearch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.MotionEventCompat;

import com.ochess.edict.R;
import com.ochess.edict.util.ScreenUtil;

import java.util.HashMap;

/**
 * Created by Basit on 12/10/2016.
 */

public class WordSearchView extends View {

    private int rows;
    private int columns;
    private int width;

    private char[][] letters;
    private Word[] words;

    private Cell[][] cells;
    private Cell cellDragFrom, cellDragTo;

    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint highlighterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final HashMap<String,Paint> highlighterPaints = new HashMap<>();
    private final Paint gridLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Typeface typeface;

    private OnWordSearchedListener onWordSearchedListener;
    private int wordsSearched = 0;
    private final int[] highlighterColors = {0x4400649C, 0x44ffd900, 0x447fbb00};

    public WordSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        textPaint.setSubpixelText(true);
        textPaint.setColor(0xcc000000);
        textPaint.setTextSize(70);
        textPaint.setTextAlign(Paint.Align.LEFT);
        if(typeface != null) {
            textPaint.setTypeface(typeface);
        }
//        textPaint.setAlpha(foregroundOpacity);

        highlighterPaint.setStyle(Paint.Style.STROKE);
        highlighterPaint.setStrokeWidth(110);
        highlighterPaint.setStrokeCap(Paint.Cap.ROUND);
        highlighterPaint.setColor(0x4400649C);

        gridLinePaint.setStyle(Paint.Style.STROKE);
        gridLinePaint.setStrokeWidth(4);
        gridLinePaint.setStrokeCap(Paint.Cap.SQUARE);
        gridLinePaint.setColor(0x11000000);
        onInit();
    }
    //// 初始化的一些数据修改
    protected void onInit(){
        int size = dp2px(30);
        textPaint.setTextSize(size);
        highlighterPaint.setStrokeWidth(dp2px(50));
        textPaint.setColor(this.getResources().getColor(R.color.text));
    }
    public void fixPrintSize(){
//        Point point = new Point();
//        ((WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(point);
//        if(point.x < 800 || point.y<750) {
//            textPaint.setTextSize(30);
//            highlighterPaint.setStrokeWidth(40);
//        }
        int size = dp2px(30);
        textPaint.setTextSize(size);
    }
    private int dp2px(float pxValue) {
        Context context = this.getContext();
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue*scale+0.5f);
    }
    public void setFontColor(){
        setFontColor(0xcc000000);
    }
    public void setFontColor(int color){
        textPaint.setColor(color);
    }
    public void setDrawColor(int color){
        highlighterPaint.setColor(color);
    }
    @Override
    protected void onDraw(Canvas canvas) {

        if(rows <= 0 || columns <= 0) {
            return;
        }

        // draw grid
        for(int i = 0; i < rows - 1; i++) {
            canvas.drawLine(0, cells[i][0].getRect().bottom, width, cells[i][0].getRect().bottom, gridLinePaint);
        }
        for(int i = 0; i < columns - 1; i++) {
//            canvas.drawLine(cells[0][i].getRect().right, cells[0][0].getRect().top, cells[0][i].getRect().right, cells[columns-1][0].getRect().bottom, gridLinePaint);
            canvas.drawLine(cells[0][i].getRect().right, cells[0][0].getRect().top,
                    cells[0][i].getRect().right, cells[rows-1][0].getRect().bottom, gridLinePaint);
        }

        // draw letters
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                String letter = String.valueOf(cells[i][j].getLetter());
                Rect textBounds = new Rect();
                textPaint.getTextBounds(letter, 0, 1, textBounds);
                canvas.drawText(letter, cells[i][j].getRect().centerX() - (textPaint.measureText(letter) / 2),
                        cells[i][j].getRect().centerY() + (textBounds.height() / 2), textPaint);
            }
        }

        // draw highlighter
        if(cellDragFrom != null && cellDragTo != null && isFromToValid(cellDragFrom, cellDragTo)) {
//            highlighterPaint.setColor(highlighterColors[wordsSearched]);
            canvas.drawLine(cellDragFrom.getRect().centerX(), cellDragFrom.getRect().centerY(),
                    cellDragTo.getRect().centerX() + 1, cellDragTo.getRect().centerY(), highlighterPaint);
        }

        for(Word word : words)
        {
            if(word.isHighlighted()) {
                Paint hPaint = highlighterPaints.get(word.getWord());
                if(hPaint==null) hPaint = highlighterPaint;
                canvas.drawLine(
                        cells[word.getFromRow()][word.getFromColumn()].getRect().centerX(),
                        cells[word.getFromRow()][word.getFromColumn()].getRect().centerY(),
                        cells[word.getToRow()][word.getToColumn()].getRect().centerX() + 1,
                        cells[word.getToRow()][word.getToColumn()].getRect().centerY(), hPaint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = measure(widthMeasureSpec);
        int measuredHeight = measure(heightMeasureSpec);
        int d = Math.min(measuredWidth, measuredHeight);
        measuredHeight = measuredWidth/10*rows;
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;

        initCells();
    }

    private void initCells() {
        if(rows <= 0 || columns <= 0) {
            return;
        }
        cells = new Cell[rows][columns];
        ////修改宽度
        int rectWH = width / columns;

        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                cells[i][j] = new Cell(new Rect(j * rectWH, i * rectWH, (j + 1) * rectWH, (i + 1) * rectWH), letters[i][j], i, j);
            }
        }
    }

    private int measure(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.UNSPECIFIED) {
            result = 100;
        } else {
            result = specSize;
        }
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        final int pointerIndex = MotionEventCompat.getActionIndex(event);
        final float x = MotionEventCompat.getX(event, pointerIndex);
        final float y = MotionEventCompat.getY(event, pointerIndex);

//        Log.d("WordsGrid", "x:" + x + ", y:" + y);

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            cellDragFrom = getCell(x,y);
            cellDragTo = cellDragFrom;
            invalidate();
            this.getParent().requestDisallowInterceptTouchEvent(true);

        } else if(event.getAction() == MotionEvent.ACTION_MOVE) {
            Cell cell = getCell(x,y);
            if(cellDragFrom != null && cell != null && isFromToValid(cellDragFrom, cell)) {
                cellDragTo = cell;
                invalidate();
            }
            if(event.getPointerCount()==2){
                this.getParent().requestDisallowInterceptTouchEvent(false);
            }
        } else if(event.getAction() == MotionEvent.ACTION_UP) {
//            Log.d("WordsGrid", getWordStr(cellDragFrom, cellDragTo));
            String word = getWordStr(cellDragFrom, cellDragTo);
            highlightIfContain(word);
            cellDragFrom = null;
            cellDragTo = null;
            invalidate();
            this.getParent().requestDisallowInterceptTouchEvent(false);

            return false;
        }

        return true;
    }

    private Cell getCell(float x, float y) {
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                if(cells[i][j].getRect().contains((int)x,(int)y)) {
                    return cells[i][j];
                }
            }
        }
        return null;
    }

    public void setLetters(char[][] letters) {
        this.letters = letters;
        rows = letters.length;
        columns = letters[0].length;
        initCells();
        invalidate();
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
        textPaint.setTypeface(typeface);
        invalidate();
    }

    private boolean isFromToValid(Cell cellDragFrom, Cell cellDragTo) {
        return (Math.abs(cellDragFrom.getRow() - cellDragTo.getRow()) == Math.abs(cellDragFrom.getColumn() - cellDragTo.getColumn()))
                || cellDragFrom.getRow() == cellDragTo.getRow() || cellDragFrom.getColumn() == cellDragTo.getColumn();
    }

    private static class Cell {
        private Rect rect;
        private char letter;
        private int rowIndex, columnIndex;

        public Cell(Rect rect, char letter, int rowIndex, int columnIndex) {
            this.rect = rect;
            this.letter = letter;
            this.rowIndex = rowIndex;
            this.columnIndex = columnIndex;
        }

        public Rect getRect() {
            return rect;
        }

        public void setRect(Rect rect) {
            this.rect = rect;
        }

        public char getLetter() {
            return letter;
        }

        public void setLetter(char letter) {
            this.letter = letter;
        }

        public int getRow() {
            return rowIndex;
        }

        public void setRow(int row) {
            this.rowIndex = row;
        }

        public int getColumn() {
            return columnIndex;
        }

        public void setColumn(int column) {
            this.columnIndex = column;
        }
    }

    public interface OnWordSearchedListener {
        void wordFound(String word);
    }

    public void setOnWordSearchedListener(OnWordSearchedListener onWordSearchedListener) {
        this.onWordSearchedListener = onWordSearchedListener;
    }

    public void setWords(Word... words) {
        this.words = words;
    }

    private String getWordStr(Cell from, Cell to) {
        String word = "";
        if(from.getRow() == to.getRow()) {
            int c = from.getColumn() < to.getColumn() ? from.getColumn() : to.getColumn();
            for(int i = 0; i < Math.abs(from.getColumn() - to.getColumn()) + 1; i++) {
                word += String.valueOf(cells[from.getRow()][i+c].getLetter());
            }
        } else if(from.getColumn() == to.getColumn()) {
            int r = Math.min(from.getRow(), to.getRow());
            for(int i = 0; i < Math.abs(from.getRow() - to.getRow()) + 1; i++) {
                word += String.valueOf(cells[i+r][to.getColumn()].getLetter());
            }
        } else {
            if(from.getRow() > to.getRow()) {
                Cell cell = from;
                from = to;
                to = cell;
            }
            for(int i = 0; i < Math.abs(from.getRow() - to.getRow()) + 1; i++) {
                int foo = from.getColumn() < to.getColumn() ? i : -i;
                word += String.valueOf(cells[from.getRow()+i][from.getColumn()+foo].getLetter());
            }
        }
        return word;
    }

    private void highlightIfContain(String str) {
        for(Word word : words) {
            if(word.getWord().equals(str)) {
                if(onWordSearchedListener != null) {
                    Paint newPaint = new Paint();
                    newPaint.setColor(highlighterPaint.getColor());
                    newPaint.setStyle(highlighterPaint.getStyle());
                    newPaint.setStrokeWidth(highlighterPaint.getStrokeWidth());
                    newPaint.setStrokeCap(highlighterPaint.getStrokeCap());
                    highlighterPaints.put(word.getWord(),newPaint);

                    onWordSearchedListener.wordFound(str);
                }
                word.setHighlighted(true);
                wordsSearched++;
                break;
            }
        }
    }
}
