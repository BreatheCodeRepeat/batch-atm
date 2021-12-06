package com.batch.atm.operator.batch.tokenizers;

import lombok.AllArgsConstructor;
import org.springframework.batch.item.file.transform.RegexLineTokenizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
public class AccountDetailsRegexTokenizer extends RegexLineTokenizer {
    private Pattern pattern;

    @Override
    protected List<String> doTokenize(String line) {
        Matcher matcher = this.pattern.matcher(line);
        List<String> tokens = new ArrayList<>(3);
        int findings = 0;
        while (matcher.find()){
            findings++;
            tokens.add(matcher.group());
        }
        if(findings == 0){
            return Collections.emptyList();
        }
        return tokens;
    }
}
