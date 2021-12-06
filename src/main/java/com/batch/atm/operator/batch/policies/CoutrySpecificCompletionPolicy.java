//package com.batch.atm.operator.steps.policies;
//
//
//import org.springframework.batch.item.NonTransientResourceException;
//import org.springframework.batch.item.ParseException;
//import org.springframework.batch.item.UnexpectedInputException;
//import org.springframework.batch.repeat.RepeatContext;
//import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
//
//public class CountryPeekingCompletionPolicyReader extends SimpleCompletionPolicy implements ItemReader<CountrySpecificItem> {
//
//    private PeekableItemReader<? extends CountrySpecificItem> delegate;
//
//    private CountrySpecificItem currentReadItem = null;
//
//    @Override
//    public CountrySpecificItem read() throws UnexpectedInputException, ParseException, NonTransientResourceException, Exception {
//        currentReadItem = delegate.read();
//        return currentReadItem;
//    }
//
//    @Override
//    public RepeatContext start(final RepeatContext context) {
//        return new ComparisonPolicyTerminationContext(context);
//    }
//
//    protected class ComparisonPolicyTerminationContext extends SimpleTerminationContext {
//
//        public ComparisonPolicyTerminationContext(final RepeatContext context) {
//            super(context);
//        }
//
//        @Override
//        public boolean isComplete() {
//            final CountrySpecificItem nextReadItem = delegate.peek();
//
//            // logic to check if same country
//            if (currentReadItem.isSameCountry(nextReadItem)) {
//                return false;
//            }
//
//            return true;
//        }
//    }
//}
////        }
//    }
//}