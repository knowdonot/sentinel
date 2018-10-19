package com.wangduo.sentineltest.controller.test;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: wangduo
 * @Date: 2018/9/28 13:54
 * @Description: 代码限流
 */
public class CodeSentinel {

    public static void main(String[] args) {
        CodeSentinel codeSentinel = new CodeSentinel();
        // 次数限定
        codeSentinel.ruleCount(3);

    }


    /**
     * 对代码片段限定访问次数
     */
    public void ruleCount(int count) {
        // 制定限流规则
        List<FlowRule> rules = new ArrayList<FlowRule>();
        FlowRule rule = new FlowRule();
        rule.setResource("HelloWorld");
        rule.setCount(count);//限定访问次数
        // 流量控制主要有两种统计类型，一种是统计线程数，另外一种则是统计 QPS。类型由 FlowRule.grade 字段来定义。
        // 其中，0 代表根据并发数量来限流，1 代表根据 QPS 来进行流量控制。其中线程数、QPS 值，都是由 StatisticSlot 实时统计获取的。
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
        Entry entry = null;
        try {
            // 执行次数
            for (int i = 0; i < 10; i++) {
                entry = SphU.entry("HelloWorld");
                // 对下列代码保护，限流操作
                System.out.println("hello world");
            }
        } catch (BlockException e) {
            // 被限流
            System.out.println("BlockException.....");
        } finally {
            // 确保资源退出
            if (entry != null) {
                entry.exit();
            }
        }
    }
}
