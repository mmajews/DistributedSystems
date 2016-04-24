package impl;


import Ice.Current;
import Zad1._IEchoDisp;

public class Echo extends _IEchoDisp {
    private String text = "";

    @Override
    public String removeLastLetter(Current __current) {
        if (text.length() >= 1) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }

    @Override
    public String appendString(String toAppend, Current __current) {
        text = text + toAppend;
        return text;
    }

    public String getString() {
        return text;
    }
}
