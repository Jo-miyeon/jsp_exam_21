package test;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import board.article.ArticleDao;
import board.member.Member;
import board.member.MemberDao;


@WebServlet("/member")
public class MemberController extends HttpServlet {
	MemberDao mdao = new MemberDao();
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		
		String action = request.getParameter("action");
		String dest = "";
		
		if(action.equals("showLogin")) {
			dest="loginForm.jsp";
		}else if(action.equals("doLogin")) {
			doLogin(request,response);
		}else if(action.equals("showMember")) {
			dest="memberForm.jsp";
		}else if(action.equals("doInsertMember")) {
			dest = doInsertMember(request,response);
		}else if(action.equals("error")) {
			dest = "loginFailed.jsp";
		}
		//forwarding 지금까지 하고 있던 방식 requestDispatcher를 이용해서 forward함 (a가 b에게 요청을 위임)
		//redirecting 새로 요청 
		if(dest.startsWith("redirect:")) {
			//리다이렉팅 
			String[] bits = dest.split("");
			String url = bits[1];
			response.sendRedirect(url);
		}else {
			//forwarding
			RequestDispatcher rd = request.getRequestDispatcher(dest);
			rd.forward(request, response);
		}
	}
	private String doInsertMember(HttpServletRequest request, HttpServletResponse response) {
		String loginId = request.getParameter("loginId");
		String loginPw = request.getParameter("loginPw");
		String nickname = request.getParameter("nickname");
		mdao.insertMember(loginId, loginPw, nickname);
		return "loginForm.jsp";
	}
	private String doLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String loginId = request.getParameter("loginId");
		String loginPw = request.getParameter("loginPw");
		Member loginedMember = mdao.getMemberByLoginIdAndLoginPw(loginId, loginPw);
		if(loginedMember != null) {
			//session저장소에 저장하는 법 httpsession은 session에 대한 정보들이 저장되어있고 저장소로써 쓸 수있다.
			HttpSession session = request.getSession();
			session.setAttribute("loginedMember",loginedMember);
			
			//request.setAttribute("loginedMember", loginedMember);
			
			return "redirect:http://localhost:8080/web-exam1/article?action=list"; 
			//redirect는 jsp로 하는게 아니라 servlet을 다시 태우는것jsp가 아니기 때문에 redirect를 앞에다가 구분해주는것 
		}else {
			return "loginFailed.jsp";
		}
	}
}
