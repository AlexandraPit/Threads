module org.example.pt2024_30423_pitaru_alexandra_assignment_2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example to javafx.fxml;
    exports org.example;
    exports org.example.View;
    opens org.example.View to javafx.fxml;
}