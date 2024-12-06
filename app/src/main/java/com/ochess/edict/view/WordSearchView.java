package com.ochess.edict.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import com.ochess.edict.util.ActivityRun;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

public class WordSearchView
        extends com.ochess.edict.view.wordsearch.WordSearchView
//        extends com.rjbasitali.wordsearch.WordSearchView
{
    char[][] box = new char[10][10];
    char[] nousedChars;
    public boolean isUpperCase = true;
    public ArrayList<Word> words = new ArrayList<>();
    private int rowCount=10;

    public WordSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    class Rand extends Random {
        private final String word;
        public int x;
        public int y;
        public int X;
        public int Y;
        public boolean reverse=false;

        public Rand(String word) throws Exception {
            super();
            this.word = word;
            int tn = 1000;
            while (!init() && tn-- > 0) ;
            if (tn <= 0) {
                throw new Exception("add word error:" + word, new Throwable());
            }
        }

        boolean init() {

            int f = nextInt(4), dir = nextInt(2), size = word.length();
            char[] wordChild = word.toCharArray();
            reverse=(dir == 1);
            if(size>=10){
                if(f%2==1) f = f-1;
                x = nextInt(rowCount - size);
                y = nextInt(10);
            }else {
                x = nextInt(rowCount - size);
                y = nextInt(10 - size);
            }
            X = x;
            Y = y;
            switch (f) {
                case 0:  //横向
                    X = x + size - 1;
                    break;
                case 1:
                    Y = y + size - 1;
                    break;
                case 2:
                    X = x + size - 1;
                    Y = y + size - 1;
                    break;
                case 3:
                    X = x + (size - 1);
                    Y = y - (size - 1);
                    if (Y < 0) {
                        Y = y;
                        y = Y + size - 1;
                    }
                    reverse = !reverse;
            }
            if (reverse) {
                wordChild = new StringBuffer(word).reverse().toString().toCharArray();
                reverse=true;
            }
            boolean rt = boxSet(f, wordChild);
            if (reverse) {
                int xx = x, yy = y;
                x = X;
                y = Y;
                X = xx;
                Y = yy;
            }
            return rt;
        }

        boolean boxSet(int f, char[] wordChar) {
            ArrayList<Integer> sets = new ArrayList<>();
            for (int i = x, j = y, n = 0; n < wordChar.length; n++) {
                if (box[i][j] != 0 && box[i][j] != wordChar[n]) {
                    while (!sets.isEmpty()) {
                        i = sets.get(0);
                        sets.remove(0);
                        j = sets.get(0);
                        sets.remove(0);
                        box[i][j] = 0;
                    }
                    return false;
                }
                if (box[i][j] == 0) {
                    box[i][j] = wordChar[n];
                    sets.add(i);
                    sets.add(j);
                }
                switch (f) {
                    case 0:
                        i++;
                        break;
                    case 1:
                        j++;
                        break;
                    case 2:
                        i++;
                        j++;
                        break;
                    case 3:
                        i++;
                        j--;
                        break;
                    default:
                }
            }
            return true;
        }

    }

    char getRandChar() {
        if (nousedChars == null) {
            String charAll = "abcdefghijklmnopqrstuvwxyz";
            if (isUpperCase) {
                charAll = charAll.toUpperCase();
            }
            char[] has = charAll.toCharArray();
            int i = 100, n = 0;
            while (i-- > 0) {
                char c = box[i / 10][i % 10];
                if (c == 0 || c==' ' || c == '-' || c == '/') continue;
                int index = c - (isUpperCase ? 'A' : 'a');
                if(index<0) continue;
                if (has[index] != 0) n++;
                has[index] = 0;
            }
            if (n > 0) {
                nousedChars = new char[26 - n];
                for (n = 0, i = 0; i < has.length; i++) {
                    if (has[i] != 0) nousedChars[n++] = has[i];
                }
            } else {
                nousedChars = "abcdefghijklmnopqrstuvwxyz".toUpperCase().toCharArray();
            }
        }

        int index = new Random().nextInt(nousedChars.length);
        return nousedChars[index];
    }

    public class Word extends com.ochess.edict.view.wordsearch.Word {
        //默认选中
        private static final boolean highlighted = false;
        //反转现实
        public boolean reversed = false;

        public Word(String word, boolean highlighted) throws Exception {
            super(word, highlighted, 0, 0, 0, 0);
            initPostion();
        }

        public Word(String word) throws Exception {
            super(word, highlighted, 0, 0, 0, 0);
            initPostion();
        }

        public Word(String word, boolean b, int i, int i1, int i2, int i3) {
            super(word, b, i, i1, i2, i3);
        }

        void initPostion() throws Exception {
            String word = getWord();
            Rand pos = new Rand(word);
            setFromColumn(pos.y);
            setToColumn(pos.Y);
            setFromRow(pos.x);
            setToRow(pos.X);
            if(pos.reverse){
                reversed = pos.reverse;
                this.setWord(new StringBuffer(word).reverse().toString());
            }

             Log.d("Word", String.format("%s (%d,%d)->(%d,%d)", word, pos.y, pos.x, pos.Y, pos.X));

        }
        public String toLowerCase(){
            return (reversed) ? new StringBuffer(getWord()).reverse().toString().toLowerCase() : getWord().toLowerCase();
        }
    }
    void upBox(int size){
        char[][] boxNew = new char[size][10];
        for(int i=0;i<rowCount;i++) {
            System.arraycopy(box[i], 0, boxNew[i], 0, 10);
        }
        rowCount= size;
        box = boxNew;
    }
    public void addWord(String word) {
        word = isUpperCase ? word.toUpperCase() : word;
//        words.add(new Word(word,true));
        Word mWord;
        while(true) {
            try {
                mWord = new Word(word);
                break;
            } catch (Exception e) {
//                Toast.makeText(ActivityRun.Companion.getContext(), word + " set error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                if(true) return;
                if (word.length() > rowCount) {
                    upBox(word.length());
                }else{
                    upBox(rowCount+1);
                    e.printStackTrace();
                    if(rowCount>1000) {
                        Toast.makeText(ActivityRun.Companion.getContext(), word + " set error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        }
        words.add(mWord);
    }


    public Word getWord(String word) {
        for(Word w : words){
            if(w.getWord().equals(word)) return w;
        }
        return null;
    }

    public void setLetters(char[][] letters) {
        letters[0][0] = 0;
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < 10; j++) {
                letters[i][j] = box[i][j] == 0 ? getRandChar() : box[i][j];
            }
        }
//        letters =new char[][] {
//                "ASCDEFGHIJ".toCharArray(),
//                "AECDEFGHIJ".toCharArray(),
//                "AACDEFGHIJ".toCharArray(),
//                "ARCWEFGHIJ".toCharArray(),
//                "ACCDOFGHIJ".toCharArray(),
//                "AHCGERGHIJ".toCharArray(),
//                "AICDEFDHIJ".toCharArray(),
//                "ANCDEFGHIJ".toCharArray(),
//                "AGCSOMEHIJ".toCharArray(),
//                "ABCDEFGHIJ".toCharArray()
//        };
        super.setLetters(letters);
//        Word[] myWords = new Word[]{
//                new Word("ABC", false, 9, 0, 9, 2),
//                new Word("WORD", false, 3, 3, 6, 6),
//                new Word("GOF", false, 5, 3, 3, 5),
//                new Word("DEF", false, 6, 3, 6, 5),
//                new Word("SOME", false, 8, 3, 8, 6),
//                new Word("SEARCHING", false, 0, 1, 8, 1),
//        };
        Word[] myWords = new Word[words.size()];
        words.toArray(myWords);
        setWords(myWords);
    }


    public void show() {
        show(false);
    }
    public void show(boolean isPrint) {
        char[][] letters  = new char[rowCount][10];
        setLetters(letters);
        if(isPrint) {
            setFontColor();
            fixPrintSize();
        }
        changeDrawColor();
    }

    public void changeDrawColor(){
        int[] colors = new int[]{Color.RED,Color.CYAN,Color.BLUE,Color.GREEN,Color.YELLOW};
        int color = colors[ new Random().nextInt(colors.length)]&0x33FFFFFF;
        setDrawColor(color);
    }
    public void show(String word) {
        Word w = getWord(word.toUpperCase());
        if(w == null) w = getWord(new StringBuffer(word.toUpperCase()).reverse().toString());

        if(w!=null) {
            w.setHighlighted(!w.isHighlighted());
            invalidate();
        }
    }
    @NotNull
    public String[] getWordByStrings() {
        char[][] letters  = new char[10][rowCount];
        String[] rt=new String[rowCount];
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < 10; j++) {
                letters[i][j] = box[i][j] == 0 ? getRandChar() : box[i][j];
            }
            rt[i] = String.valueOf(letters[i]);
        }
        return rt;
    }
}
