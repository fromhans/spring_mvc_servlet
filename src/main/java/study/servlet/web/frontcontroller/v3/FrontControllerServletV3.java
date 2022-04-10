package study.servlet.web.frontcontroller.v3;

import com.fasterxml.jackson.databind.node.ObjectNode;
import study.servlet.web.frontcontroller.ModelView;
import study.servlet.web.frontcontroller.MyView;
import study.servlet.web.frontcontroller.v2.ControllerV2;
import study.servlet.web.frontcontroller.v2.controller.MemberFormControllerV2;
import study.servlet.web.frontcontroller.v2.controller.MemberListControllerV2;
import study.servlet.web.frontcontroller.v2.controller.MemberSaveControllerV2;
import study.servlet.web.frontcontroller.v3.controller.MemberFormControllerV3;
import study.servlet.web.frontcontroller.v3.controller.MemberListControllerV3;
import study.servlet.web.frontcontroller.v3.controller.MemberSaveControllerV3;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name ="frontControllerServletV3", urlPatterns ="/front-controller/v3/*")
public class FrontControllerServletV3 extends HttpServlet {

    private Map<String, ControllerV3> controllerMap = new HashMap<>();

    public FrontControllerServletV3() {
        controllerMap.put("/front-controller/v3/members/new-form", new MemberFormControllerV3());
        controllerMap.put("/front-controller/v3/members/save", new MemberSaveControllerV3());
        controllerMap.put("/front-controller/v3/members", new MemberListControllerV3());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("FrontControllerServletV3.service");

        String requestURI = request.getRequestURI();
        ControllerV3 controller = controllerMap.get(requestURI);
        if(controller == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        //paramMap
        //Iterator를 통해 함수형 인터페이스로 request 객체의 attributes를 Map에 Key-Value형태로 넣음.
        Map<String, String> paramMap = createParamMap(request);
        // 런타임에는 controller에 구현체가 들어가게되어, 비지니스 로직이 정의된 process가 수행됨.
        // process는 ModelView 객체를 반환하며, ModelView 객체는 View의 논리적 이름(jsp 파일의 논리 이름)과
        // request 객체의 attribute들이 담겨있는 paramMap에 View에서 필요한 key-value를 추가하여 반환해준다.
        // ModelView의 model은 Map<String, Object> 형태이기 때문에, MemberSaveControllerV3의 경우
        // "member" 스트링값과 실제 값이 추가된다.
        ModelView mv = controller.process(paramMap);

        String viewName = mv.getViewName(); //논리이름 new-form
        MyView view = viewResolver(viewName);
        view.render(mv.getModel(), request, response);
    }

    private MyView viewResolver(String viewName) {
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }

    private Map<String, String> createParamMap(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        request.getParameterNames().asIterator()
                .forEachRemaining(paramName->paramMap.put(paramName, request.getParameter(paramName)));
        return paramMap;
    }
}
