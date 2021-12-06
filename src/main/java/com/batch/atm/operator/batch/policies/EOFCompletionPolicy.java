//package com.batch.atm.operator.steps.policies;
//
//import com.batch.atm.operator.model.UserSession;
//import org.springframework.batch.item.NonTransientResourceException;
//import org.springframework.batch.item.PeekableItemReader;
//import org.springframework.batch.repeat.RepeatContext;
//import org.springframework.batch.repeat.context.RepeatContextSupport;
//import org.springframework.batch.repeat.policy.CompletionPolicySupport;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//public class EOFCompletionPolicy extends CompletionPolicySupport {
//    private EOFCompletionContext cc;
//    private PeekableItemReader<UserSession> reader;
//
//    public void setReader(PeekableItemReader<UserSession> forseeingReader)
//    {
//        this.reader = forseeingReader;
//    }
//
//    @Override
//    public boolean isComplete(RepeatContext context)
//    {
//        return this.cc.isComplete();
//    }
//
//    @Override
//    public RepeatContext start(RepeatContext context)
//    {
//        this.cc = new EOFCompletionContext(context);
//        return cc;
//    }
//
//    @Override
//    public void update(RepeatContext context)
//    {
//        this.cc.update();
//    }
//
//    protected class EOFCompletionContext extends RepeatContextSupport
//    {
//        boolean eof = false;
//        public EOFCompletionContext (RepeatContext context)
//        {
//            super(context);
//        }
//
//        public void update()
//        {
//            final String next;
//            try
//            {
//                next = reader.peek();
//            }
//            catch (Exception e)
//            {
//                throw new NonTransientResourceException("Unable to peek", e);
//            }
//            // EOF?
//            this.eof = next.equals("\n");
//        }
//
//        public boolean isComplete() {
//            return this.eof;
//        }
//    }
//}