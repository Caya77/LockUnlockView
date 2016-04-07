package testb.hello.pic;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Edgar.Qi on 2016/4/6.
 */
public class KeyboardUtil {

    private KeyboardView keyboardView;
    private Keyboard number_keyboard;
    private EditText ed;

    public KeyboardUtil(Activity act, Context ctx, EditText edit) {
        this.ed = edit;
        number_keyboard = new Keyboard(ctx, R.xml.symbols);
        keyboardView = (KeyboardView) act.findViewById(R.id.keyboard_view);
        keyboardView.setKeyboard(number_keyboard);
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(true);
        keyboardView.setVisibility(View.VISIBLE);
        keyboardView.setOnKeyboardActionListener(listener);
    }

    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onPress(int primaryCode) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Editable editable = ed.getText();
            int start = ed.getSelectionStart();
            if (primaryCode == Keyboard.KEYCODE_DELETE) {
                //回退
                if (editable != null && editable.length() > 0) {
                    if (start > 0) {
                        editable.delete(start - 1, start);
                    }
                }
            } else if (primaryCode == 4896) {
                //清空
                editable.clear();
            } else {
                //输入
                editable.insert(start, Character.toString((char) primaryCode));
            }
        }
    };

    public void showKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            keyboardView.setVisibility(View.VISIBLE);
        }
    }
}
