package submit;

class TestFaintness {
    /**
     * In this method all variables are faint because the final value is never used.
     * Sample out is at src/test/Faintness.out
     */
    void test1() {
        int x = 2;
        int y = x + 2;
        int z = x + y;
        return;
    }

    /**
     * Write your test cases here. Create as many methods as you want.
     * Run the test from root dir using
     * bin/parun flow.Flow submit.MySolver submit.Faintness submit.TestFaintness
     */

    // Faint Var: x, because y is used in return and i is used in loop test
    int test2() {
        int y = 0;
        int i = 0;
        int x = 0;
        while(i < 3) {
            x = x + 1;
            i += 1;
            y = y + 1;
        }
        return y;
    }

    // Faint Var: x,z, because y is used in return
    int test3() {
        int x = 2;
        int y = x + 2;
        int z = x + y;
        return y;
    }
    
    // Faint Var: none 
    int test4() {
        int y = 2;
        y = y + 2;
        return y;
    }

}
