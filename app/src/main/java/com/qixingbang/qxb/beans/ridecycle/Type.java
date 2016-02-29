package com.qixingbang.qxb.beans.ridecycle;

/**
 * Created by zqj on 2015/11/12 10:50.
 */
public enum Type {
    NEWS {
        @Override
        public String toString() {
            return "news";
        }

        @Override
        public int getIndex() {
            return 0;
        }
    },
    CARE {
        @Override
        public String toString() {
            return "care";
        }

        @Override
        public int getIndex() {
            return 1;
        }
    },
    STRATEGY {
        @Override
        public String toString() {
            return "strategy";
        }

        @Override
        public int getIndex() {
            return 2;
        }
    },
    DRY_CARGO {
        @Override
        public String toString() {
            return "dryCargo";
        }

        @Override
        public int getIndex() {
            return 3;
        }
    };


    public abstract String toString();

    public abstract int getIndex();

    public static Type get(int index) {
        if (index == 0) {
            return NEWS;
        } else if (index == 1) {
            return CARE;
        } else if (index == 2) {
            return STRATEGY;
        } else {
            return DRY_CARGO;
        }
    }
}
