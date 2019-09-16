package com.client;

public class StateManage {
    private static final int STATUS_1 = 1;
    private static final int STATUS_2 = 1 << 1;
    private static final int STATUS_3 = 1 << 2;
    private static final int STATUS_4 = 1 << 3;
    private static final int STATUS_5 = 1 << 4;
    private static final int STATUS_6 = 1 << 5;
    private static final int STATUS_7 = 1 << 6;
    private static final int STATUS_8 = 1 << 7;

    private final int MODE_A = STATUS_1 | STATUS_2 | STATUS_3;
    private final int MODE_B = STATUS_1 | STATUS_4 | STATUS_5 | STATUS_6;
    private final int MODE_C = STATUS_1 | STATUS_7 | STATUS_8;

    private int curState;

    private void addState(int state){
        this.curState |= state;
    }

    private void removeState(int state){
        this.curState &= ~state;
    }

    public boolean isStatusEnabled(int state) {
        return (this.curState & state) != 0;
    }

    public boolean checkMode(int mode){
        return curState == mode;
    }
}
