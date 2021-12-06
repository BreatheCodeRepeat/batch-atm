//package com.batch.atm.operator.steps.policies;
//
//import org.springframework.batch.item.file.separator.SimpleRecordSeparatorPolicy;
//import org.springframework.batch.repeat.CompletionPolicy;
//import org.springframework.batch.repeat.RepeatContext;
//import org.springframework.batch.repeat.RepeatStatus;
//
//public class BlankLineCompletionPolicy extends CompletionPolicy {
//
//    @Override
//    public boolean isEndOfRecord(final String line) {
//        return line.trim().length() != 0 && super.isEndOfRecord(line);
//    }
//
//    @Override
//    public String postProcess(final String record) {
//        if (record == null || record.trim().length() == 0) {
//            return null;
//        }
//        return super.postProcess(record);
//    }
//
//    @Override
//    public boolean isComplete(RepeatContext repeatContext, RepeatStatus repeatStatus) {
//        return false;
//    }
//
//    @Override
//    public boolean isComplete(RepeatContext repeatContext) {
//        return false;
//    }
//
//    @Override
//    public RepeatContext start(RepeatContext repeatContext) {
//        return null;
//    }
//
//    @Override
//    public void update(RepeatContext repeatContext) {
//
//    }
//}
