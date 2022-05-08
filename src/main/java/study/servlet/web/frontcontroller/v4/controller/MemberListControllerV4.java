package study.servlet.web.frontcontroller.v4.controller;

import study.servlet.domain.Member;
import study.servlet.domain.MemberRepository;
import study.servlet.web.frontcontroller.ModelView;
import study.servlet.web.frontcontroller.v3.ControllerV3;
import study.servlet.web.frontcontroller.v4.ControllerV4;

import java.util.List;
import java.util.Map;

public class MemberListControllerV4 implements ControllerV4 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public String process(Map<String, String> paramMap, Map<String, Object> model) {
        List<Member> members = memberRepository.findAll();
        model.put("members", members);
        return "members";
    }
}
