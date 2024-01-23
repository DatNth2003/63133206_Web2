package BMICalc;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet implementation class BMI
 */
public class BMI extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    public enum Region {
        EUROPE,
        ASIA,
        GLOBAL
    }
    public enum Gender {
    	NONE,// không xét
        MALE,
        FEMALE
        
    }
    public enum Unit {
        M,
        CM
    }
    Region region;
    Gender gender;
    Unit unit;
    String name = "bạn";
    int age = 0;
    double bmi = -1;
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Render BMI form
        showBMIForm(response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get parameters from the form
        name = request.getParameter("name");
        String ageStr = request.getParameter("age");
        String heightStr = request.getParameter("height");
        String weightStr = request.getParameter("weight");
        region = Region.valueOf(request.getParameter("region"));
        unit = Unit.valueOf(request.getParameter("unit"));

        if (ageStr != null && !ageStr.isEmpty()) {
            age = Integer.parseInt(ageStr);
        }

        if (name == null || name.isEmpty()) {
            name = "bạn";
        }

        if (heightStr != null && weightStr != null && !heightStr.isEmpty() && !weightStr.isEmpty()) {
            double height = Double.parseDouble(heightStr);
            double heightInMeters = (unit == Unit.CM) ? height / 100.0 : height;
            double weight = Double.parseDouble(weightStr);
            bmi = BMICalc(heightInMeters, weight);
            System.out.println("Height: " + heightInMeters + ", Weight: " + weight + ", BMI: " + bmi);
            region = Region.valueOf(request.getParameter("region"));

            showBMIResult(response);
        } else {
            showBMIForm(response);
        }
    }

    private double BMICalc(double h, double w) {
        return w / (h * h);
    }
    private void formNhap(PrintWriter out) {
        out.println("<form method=\"post\" action=\"BMI\" class=\"bg-white p-4 rounded shadow\">");
        out.println("<h2 class=\"text-center\">Nhập thông tin</h2>");
        out.println("<div class=\"mb-3\">");
        out.println("<label for=\"name\" class=\"form-label\">Tên:</label>");
        out.println("<input type=\"text\" name=\"name\" class=\"form-control\">");
        out.println("</div>");

        out.println("<div class=\"mb-3\">");
        out.println("<label for=\"age\" class=\"form-label\">Tuổi:</label>");
        out.println("<input type=\"number\" name=\"age\" class=\"form-control\" min=\"0\" max=\"150\" step=\"1\">");
        out.println("</div>");
        
        out.println("<div class=\"mb-3\">");
        out.println("<label for=\"height\" class=\"form-label\">Chiều cao:</label>");
        out.println("<input type=\"text\" name=\"height\" class=\"form-control\" required>");
        out.println("</div>");

        out.println("<div class=\"mb-3\">");
        for (Unit u : Unit.values()) {
            out.println("<div class=\"form-check form-check-inline\">");
            out.println("<input class=\"form-check-input\" type=\"radio\" name=\"unit\" value=\"" + u.name() + "\" " +
                    (u == Unit.M ? "checked" : "") + " required>");
            out.println("<label class=\"form-check-label\">" + u.name() + "</label>");
            out.println("</div>");
        }
        out.println("</div>");

        out.println("<div class=\"mb-3\">");
        out.println("<label for=\"weight\" class=\"form-label\">Cân nặng:</label>");
        out.println("<input type=\"text\" name=\"weight\" class=\"form-control\" required>");
        out.println("</div>");
        
        out.println("<div class=\"mb-3\">");
        out.println("<label for=\"weight\" class=\"form-label\">Giới tính:</label>");
        // Male
        out.println("<div class=\"form-check\">");
        out.println("<input class=\"form-check-input\" type=\"radio\" name=\"gender\" value=\"Nam\" " + (gender == Gender.MALE ? "checked" : "") + " required>");
        out.println("<label class=\"form-check-label\">Nam</label>");
        out.println("</div>");

        // Female
        out.println("<div class=\"form-check\">");
        out.println("<input class=\"form-check-input\" type=\"radio\" name=\"gender\" value=\"Nữ\" " + (gender == Gender.FEMALE ? "checked" : "") + " required>");
        out.println("<label class=\"form-check-label\">Nữ</label>");
        out.println("</div>");

        // None
        out.println("<div class=\"form-check\">");
        out.println("<input class=\"form-check-input\" type=\"radio\" name=\"gender\" value=\"None\" " + (gender != Gender.MALE && gender != Gender.FEMALE ? "checked" : "") + " required>");
        out.println("<label class=\"form-check-label\">Không xét</label>");
        out.println("</div>");
        
        out.println("</div>");

        out.println("<div class=\"mb-3\">");
        out.println("<label for=\"region\" class=\"form-label\">Khu vực:</label>");
        out.println("<select name=\"region\" class=\"form-select\" required>");
        out.println("<option value=\"GLOBAL\" " + (region == Region.GLOBAL ? "selected" : "selected") + ">Chung</option>");
        out.println("<option value=\"ASIA\" " + (region == Region.ASIA ? "selected" : "") + ">Châu Á</option>");
        out.println("<option value=\"EUROPE\" " + (region == Region.EUROPE ? "selected" : "") + ">Châu Âu</option>");
        out.println("</select>");
        out.println("</div>");
        out.println("<button type=\"submit\" class=\"btn btn-primary\" onclick=\"updateForm('" + name + "', " + age + ", '" + gender + "', 0, '');\">Tính BMI</button>");
        out.println("</form>");
        
    }
    
    private void showBMIForm(HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<title>BMI Calculator</title>");
        out.println("<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css\">");
        out.println("</head>");
        out.println("<body class=\"bg-light\">");
        out.println("<div class=\"container py-5\">");
        out.println("<div class=\"row\">");

        out.println("<div class=\"col-md-6 order-md-1 mx-auto\">");
        formNhap(out);

        out.println("</div>");

        out.println("<div class=\"col-md-6 order-md-2 mx-auto\">");
        showBMIResultContent(out);
        out.println("</div>");

        out.println("</div>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }

    private void showBMIResult(HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<title>BMI Result</title>");
        out.println("<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css\">");
        out.println("<script>");
        out.println("function updateForm() {");
        out.println("$.ajax({");
        out.println("url: '/your-app-context/BMI',"); // Update with your actual app context
        out.println("type: 'POST',");
        out.println("data: $('#yourFormId').serialize(),"); // Update with your actual form ID
        out.println("success: function(data) {");
        out.println("if (data.status === 'success') {");
        out.println("$('#yourFormId').html(data.form);"); // Update with your actual form ID
        out.println("} else {");
        out.println("alert('Error: ' + data.message);");
        out.println("}");
        out.println("}");
        out.println("});");
        out.println("}");
        out.println("</script>");
        out.println("</head>");
        out.println("<body class=\"bg-light\">");
        out.println("<div class=\"container py-5\">");
        out.println("<div class=\"row\">");

        out.println("<div class=\"col-md-6 order-md-2 mx-auto\">");
        showBMIResultContent(out);
        out.println("</div>");

        out.println("<div class=\"col-md-6 order-md-1 mx-auto\">");
        out.println("<form id=\"yourFormId\" method=\"post\" action=\"BMI\" class=\"bg-white p-4 rounded shadow\">");
        formNhap(out);
        out.println("</form>");
        out.println("</div>");

        out.println("</div>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }

    private void showBMIResultContent(PrintWriter out) {
        out.println("<form method=\"post\" action=\"BMIResult\" class=\"bg-white p-4 rounded shadow\">");
        out.println("<h2 class=\"mb-4 text-center\">Kết quả BMI</h2>");
        if(name == null) name = "bạn";
        out.println("<p id=\"rGreeting\">Chào " + name + "!</p>");

        if (age > 0) {
            out.println("<p id=\"rAge\">Tuổi: " + age + " tuổi</p>");
        }
        if (gender != null) {
            out.println("<p id=\"rGender\">Giới tính: " + gender + "</p>");
        }
        if (region != null) {
            out.println("<p id=\"rRegion\">Đến từ: " + region + "</p>");
        }
        if(bmi >=0) {
        	out.println("<p id=\"rBMI\">BMI: " + bmi + "</p>");
        	String bmiResult = layPhanLoaiBMI(bmi, age, gender);
            if(bmiResult!=null) out.println("<p><strong>Phân loại:</strong> <span id=\"rClass\"><b>" + bmiResult + "</b></span></p>");
        }
        else {
        	out.println("<p>Mời bạn nhập chiều cao cân nặng để tôi tính BMI!</p>");
        }
        
        out.println("</form>");
    }

    private String layPhanLoaiBMI(double bmi, int age, Gender gender) {
        double[] nguongChung = new double[] {18.5, 24.9, 29.9, 34.9, 39.9, 40.0, Double.POSITIVE_INFINITY };;
        double[] nguongChauAu = new double[] {18.5, 24.9, 29.9, 34.9, 39.9, 40.0, Double.POSITIVE_INFINITY};
        double[] nguongChauA = new double[] {18.5, 24.9, 29.9, 34.9, 39.9, 40.0, Double.POSITIVE_INFINITY};
        double[] nguong;
        if (region == Region.EUROPE) {
        	nguong = nguongChauAu;
        } else if (region == Region.ASIA) {
        	nguong = nguongChauA;
        } else {
            nguong = nguongChung;
            
        }
        
        if (age >= 60) {
            nguong[0] = 22.0;
        }

        return tinhLoaiBMI(bmi, nguong);
    }

    private String tinhLoaiBMI(double bmi, double[] nguong) {
    	if (bmi < nguong[0]) {
            return "<span style='color: #FFD700;'>Gầy</span>";
        } else if (bmi < nguong[1]) {
            return "<span style='color: #008000;'>Tiêu chuẩn</span>";
        } else if (bmi < nguong[2]) {
            return "<span style='color: #FFD700;'>Thừa cân</span>";
        } else if (bmi < nguong[3]) {
            return "<span style='color: #FFA500;'>Tiền béo phì</span>";
        } else if (bmi < 30) {
            return "<span style='color: #FF4500;'>Béo phì loại 1</span>";
        } else if (bmi < 35) {
            return "<span style='color: #DC143C;'>Béo phì loại 2</span>";
        } else if (bmi < 40) {
            return "<span style='color: #800000;'>Béo phì loại 3</span>";
        } else {
            return "<span style='color: #8B0000;'>Béo phì loại 4</span>";
        }
    }
}
