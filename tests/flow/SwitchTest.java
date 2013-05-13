import sparta.checkers.quals.*;
import sparta.checkers.quals.FlowPermission;
import sparta.checkers.quals.FlowPermission;
import sparta.checkers.quals.Sink;
import sparta.checkers.quals.PolySink;
import sparta.checkers.quals.PolySource;

class SwitchTest {
    void foo() {
        @Source({FlowPermission.ACCESS_FINE_LOCATION, FlowPermission.LITERAL}) int info = 1;

        //Explicitly forbid this field from having FlowPermission.CONDITIONAL
        //:: error: (forbidden.flow)
        @Source({FlowPermission.ACCESS_FINE_LOCATION}) @Sink({}) int badInfo = 1;

        @Source(FlowPermission.LITERAL) int noInfo = 1;

        //This field gets FlowPermission.CONDITIONAL added by default
        final @Source({FlowPermission.ACCESS_FINE_LOCATION, FlowPermission.LITERAL}) int caseInfo = 1;
        final int caseNoInfo = 2;


        //Explicitly forbid this field from having FlowPermission.CONDITIONAL
        //:: error: (forbidden.flow)
        final @Source({FlowPermission.ACCESS_FINE_LOCATION, FlowPermission.LITERAL}) @Sink({}) int badCaseInfo = 3;

        switch (info) {

            case caseInfo: {
                info++;
            }
            case caseNoInfo: {
                info++;
            }
        }

        //:: error: (condition.flow)
        switch (badInfo) {
            //:: error: (condition.flow)
            case badCaseInfo: {
                info++;
            }
            case caseNoInfo: {
                info++;
            }
        }

        switch (noInfo) {
            //:: error: (condition.flow)
            case badCaseInfo: {
                info++;
            }

            case caseInfo: {
                info++;
            }

            case caseNoInfo: {
                info++;
            }
        }
    }
}
