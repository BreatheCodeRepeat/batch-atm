//package com.batch.atm.operator.steps;
//
//import com.batch.atm.operator.model.Transaction;
//import javafx.scene.shape.LineTo;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.batch.core.StepExecution;
//import org.springframework.batch.core.annotation.AfterStep;
//import org.springframework.batch.core.annotation.BeforeStep;
//import org.springframework.batch.item.ItemReader;
//import org.springframework.batch.item.NonTransientResourceException;
//import org.springframework.batch.item.ParseException;
//import org.springframework.batch.item.UnexpectedInputException;
//import org.springframework.batch.item.file.FlatFileItemReader;
//import org.springframework.batch.item.file.mapping.DefaultLineMapper;
//import org.springframework.batch.item.file.mapping.PassThroughFieldSetMapper;
//import org.springframework.batch.item.file.transform.*;
//import org.springframework.core.io.ClassPathResource;
//
//import javax.persistence.criteria.Order;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class OrderReaderStep implements ItemReader<Transaction> {
//
//    private static final Logger logger = LoggerFactory.getLogger(OrderReaderStep.class);
//
//    private FlatFileItemReader
//            <FieldSet> delegate;
//    private static final String FOOTER = "F*";
//    private static final String BODY = "L*";
//    private static final String HEADER = "H*";
//
//    @BeforeStep
//    public void beforeStep(StepExecution stepExecution) {
//        delegate = new FlatFileItemReader<>();
//
//        delegate.setResource(new ClassPathResource("orders.txt"));
//
//        final DefaultLineMapper
//                <FieldSet> defaultLineMapper = new DefaultLineMapper<>();
//        final PatternMatchingCompositeLineTokenizer orderFileTokenizer = new PatternMatchingCompositeLineTokenizer();
//        final Map<String, LineTokenizer> tokenizers = new HashMap<>();
//        tokenizers.put(HEADER, buildHeaderTokenizer());
//        tokenizers.put(BODY, buildBodyTokenizer());
//        tokenizers.put(FOOTER, buildFooterTokenizer());
//        orderFileTokenizer.setTokenizers(tokenizers);
//        defaultLineMapper.setLineTokenizer(orderFileTokenizer);
//        defaultLineMapper.setFieldSetMapper(new PassThroughFieldSetMapper());
//
//        delegate.setLineMapper(defaultLineMapper);
//
//        delegate.open(stepExecution.getExecutionContext());
//    }
//
//    @AfterStep
//    public void afterStep(StepExecution stepExecution) {
//        delegate.close();
//    }
//
//    @Override
//    public OrderList read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
//        logger.info("start read");
//
//        OrderList record = null;
//
//        FieldSet line;
//        List<Order> bodyList = new ArrayList<>();
//        while ((line = delegate.read()) != null) {
//            String prefix = line.readString("lineType");
//            if (prefix.equals("H")) {
//                record = new OrderList();
//                record.setName(line.readString("name"));
//            } else if (prefix.equals("L")) {
//                Order order = new Order();
//                order.setLookup(line.readString("lookupKey"));
//                order.setLookupType(line.readString("keyType"));
//                bodyList.add(order);
//            } else if (prefix.equals("F")) {
//                if (record != null) {
//                    if (line.readLong("count") != bodyList.size()) {
//                        throw new ValidationException("Size does not match file count");
//                    }
//                    record.setOrders(bodyList);
//                }
//                break;
//            }
//
//        }
//        logger.info("end read");
//        return record;
//    }
//
//    private LineTokenizer buildBodyTokenizer() {
//        FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
//
//        tokenizer.setColumns(new Range[]{ //
//                new Range(1, 1), // lineType
//                new Range(2, 2), // keyType
//                new Range(3, 12) // lookup key
//        });
//
//        tokenizer.setNames(new String[]{ //
//                "lineType",
//                "keyType",
//                "lookupKey"
//        }); //
//        tokenizer.setStrict(false);
//        return tokenizer;
//    }
//
//    private LineTokenizer buildFooterTokenizer() {
//        FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
//
//        tokenizer.setColumns(new Range[]{ //
//                new Range(1, 1), // lineType
//                new Range(2, 9) // count
//        });
//
//        tokenizer.setNames(new String[]{ //
//                "lineType",
//                "count"
//        }); //
//        tokenizer.setStrict(false);
//        return tokenizer;
//    }
//
//    private LineTokenizer buildHeaderTokenizer() {
//        FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
//
//        tokenizer.setColumns(new Range[]{ //
//                new Range(1, 1), // lineType
//                new Range(2, 20), // name
//        });
//
//        tokenizer.setNames(new String[]{ //
//                "lineType",
//                "name"
//        }); //
//        tokenizer.setStrict(false);
//        return tokenizer;
//    }
//
//}