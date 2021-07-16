import java.io.IOException;
import java.util.Optional;
import javafx.application.Application; 
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.Group;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class LoadSign extends Application {

	static private final int NODE = 17;
	
	static private final String[] NAMESPOT = {"정문", "정문입구", "대학본부", "제2학생회관", "테크노큐브", "미래관", "무궁관", "혜성관", 
			"후문", "종합운동장", "백주년기념관", "붕어방", "중앙도서관", "제1학생회관", "어학원", "어의관", "다빈치관"};
	static private final int[][] LOCATION = { {461, 525}, {485, 405}, {539, 291}, {660, 346}, {733, 406}, {875, 326},
			{801, 262}, {709, 213}, {802, 175}, {651, 458}, {589, 436}, {340, 281}, {255, 282}, {239, 256}, {228, 192}, {152, 216}, {276, 147}
	};
	static private Group lineGroup; // 경로
	static private TextField pathTf = new TextField(); // 경로 텍스트
	static private TextField meterTf = new TextField(); // 예상 거리 텍스트
	static private TextField timeTf = new TextField(); // 예상 시간 텍스트
	
	static private String[] pathNode;
	static private Pane mainPane;
	
	static private int count = 0; // 경로 검색 횟수
	static private Color color; // 경로 색깔
	//no-arg 생성자
	public LoadSign() {
	}
	
	@Override 
	public void start(Stage primaryStage) {
		mainPane = new Pane();
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(5, 5, 5, 5));
		ImageView imageView = new ImageView("file:map.jpg");
		imageView.fitHeightProperty().bind(mainPane.heightProperty());
		imageView.fitWidthProperty().bind(mainPane.widthProperty());

		pane.getChildren().add(imageView);
		pane.setBottom(getHBox(primaryStage));

		/*
		// 각 노드별 LOCATION 잡을 때, 사용한 메서드입니다.
		pane.setOnMouseClicked(e -> {
			System.out.println(e.getX() + " " + e.getY());
		});
		 */
		
		pane.prefHeight(600);
		pane.prefWidth(1000);
		
		mainPane.getChildren().addAll(pane, lineGroup);
		
		Scene scene = new Scene(mainPane, 1000, 600);
		primaryStage.setResizable(false);
		primaryStage.setTitle("SNUT_Loadsign"); // Set the stage title
		primaryStage.setScene(scene); // Place the scene in the stage
		primaryStage.show(); // Display the stage
	}

	private HBox getHBox(Stage priStage) {
		HBox hBox = new HBox();
		hBox.setPadding(new Insets(15,15,15,15)); 
		
		ObservableList<String> items = FXCollections.observableArrayList(NAMESPOT);
		ComboBox<String> startCbo = new ComboBox<>(items);
		ComboBox<String> endCbo = new ComboBox<>(items);
		
		startCbo.setValue("출발지 선택");
		endCbo.setValue("도착지 선택");
		
		Button btOK = new Button("OK");
		lineGroup = new Group();

		btOK.setOnAction( e-> {
			int start = convert(startCbo.getValue()); // comboBox에서 선택한 출발지를 배열의 고유번호로 바꿔줍니다.
			int end = convert(endCbo.getValue()); // comboBox에서 선택한 도착지를 배열의 고유번호로 바꿔줍니다.
			
			Path path = new Path(start,end); // 입력한 출발지와 도착지를 기준으로 Path를 생성합니다.

			try {
				path.calPath(); // 경로 계산
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			pathNode = path.getPathNode().split(" ", 0); // 계산된 경로(String)을 연산을 위해 배열로 만듭니다.
			changeColor(); // 경로 색깔을 변경합니다.
			
			// 캠퍼스지도상에 그릴 Line객체를 pathNode를 토대로 생성합니다.
			for(int i = 1; i < pathNode.length; i++) {
				System.out.println(pathNode[i-1]);
				Line tmpLine = new Line(LOCATION[Integer.parseInt(pathNode[i-1])-1][0], LOCATION[Integer.parseInt(pathNode[i-1])-1][1], LOCATION[Integer.parseInt(pathNode[i])-1][0], LOCATION[Integer.parseInt(pathNode[i])-1][1]);
				tmpLine.setStrokeWidth(3);
				tmpLine.setStroke(color);
				lineGroup.getChildren().add(tmpLine);
			}

			// 건물 고유숫자로 된 경로를 건물명으로 바꿉니다.
			String pathInfo = "";
			for(int i = 0; i < pathNode.length; i++) {
				pathInfo += NAMESPOT[Integer.parseInt(pathNode[i])-1];
				if(i == pathNode.length-1)
					break;
				pathInfo += " -> ";
			}
			int minute = path.getTime() / 60;
			int second = path.getTime() % 60;
			
			pathTf.setText("경로 : " + pathInfo + " (총 " + path.getNodeCount() + "곳 지남)");
			meterTf.setText("거리 : " + path.getMeter() + "m");
			timeTf.setText("예상 소요시간 : " + minute + "분 " + second + "초");
			BorderPane textPn = new BorderPane();
			pathTf.setPrefWidth(700);
			meterTf.setPrefWidth(100);
			timeTf.setPrefWidth(200);

			textPn.setTop(pathTf);
			textPn.setLeft(meterTf);
			textPn.setCenter(timeTf);

			hBox.getChildren().addAll(textPn);
		}
				);

		Button btClear = new Button("새로고침");
		btClear.setOnAction(e->{
			lineGroup = null;
			start(new Stage()); // 새 창 열기
			priStage.close(); // 이전 창 닫기
		}
				);
		//경로와 거리를 출력할 label 구현

		hBox.getChildren().addAll(startCbo, endCbo, btOK, btClear);

		return hBox;
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	private static void changeColor() {
		count = (count+1)%3;
		
		switch(count) {
		case 1:
			color = Color.RED;
			break;
		case 2:
			color = Color.BLUE;
			break;
		case 0:
			color = Color.BLACK;
			break;
		}
	}

	public int convert(String spot) {
		int result = -1; // 못 찾았을 경우, -1로 반환합니다.

		// 건물이름으로 찾습니다.
		for(int i = 0; i < NODE; i++) {
			if(spot.equals(NAMESPOT[i])) {
				result = i+1;
				break;
			}
		}
		
		// 유저가 선택하지 않았을 경우, 기본 출발지를 정문으로 설정합니다.
		if(spot.equals("출발지 선택")) {
			result = 1;
			
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("주의");
			alert.setHeaderText("출발지 미선택");
			alert.setContentText("기본 출발지인 '정문'으로 선택된 결과가 보여집니다.");

			alert.showAndWait();
		}
		if(spot.equals("도착지 선택")) {
			result = 9;
			
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("주의");
			alert.setHeaderText("도착지 미선택");
			alert.setContentText("기본 도착지인 '후문'으로 선택된 결과가 보여집니다.");

			alert.showAndWait();
		}
		
		return result;
	}
}

