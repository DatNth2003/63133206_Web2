package doPost;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet implementation class TestDoPost
 */
public class TestDoPost extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public TestDoPost() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter traVe = response.getWriter();
        traVe.append("<div style=\"text-align: center;\"><h1>Bạn vừa gửi yêu cầu dạng Get, đây là kết quả:</h1></div>");
        String noiDungHTML = "<form method=\"POST\" action=\"/DoPost/TestDoPost\" style=\"width: 300px; margin: 20px auto; padding: 10px; border: 1px solid #ccc; border-radius: 5px;\">"
                + "<label style=\"display: block; margin-bottom: 5px;\">Họ: </label>"
                + "<input type=\"text\" name=\"fname\" style=\"width: 100%; padding: 5px; margin-bottom: 10px;\">\r\n"
                + "<label style=\"display: block; margin-bottom: 5px;\">Tên: </label>"
                + "<input type=\"text\" name=\"lname\" style=\"width: 100%; padding: 5px; margin-bottom: 10px;\">\r\n"
                + "<input type=\"submit\" value=\"Gửi đi\" style=\"background-color: #4CAF50; color: white; padding: 10px; border: none; border-radius: 5px; cursor: pointer;\">\r\n"
                + "</form>";
        traVe.append(noiDungHTML);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        String firstName = request.getParameter("fname");
        String lastName = request.getParameter("lname");
        PrintWriter traVe = response.getWriter();
        traVe.append("<div style=\"text-align: center;\"><h1>Bạn vừa gửi yêu cầu dạng Post, đây là kết quả:</h1></div>");
        traVe.append("<div style=\"width: 300px; margin: 20px auto; padding: 10px; border: 1px solid #ccc; border-radius: 5px; text-align: left;\">");
        traVe.append("<label style=\"display: inline-block; width: 50px; margin-bottom: 5px;\">Họ: </label>")
                .append("<span>").append(firstName).append("</span><br>");
        traVe.append("<label style=\"display: inline-block; width: 50px; margin-bottom: 5px;\">Tên: </label>")
                .append("<span>").append(lastName).append("</span><br>");
        traVe.append("</div>");
    }
    

}
