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
	
	static private final String[] NAMESPOT = {"����", "�����Ա�", "���к���", "��2�л�ȸ��", "��ũ��ť��", "�̷���", "���ð�", "������", 
			"�Ĺ�", "���տ��", "���ֳ����", "�ؾ��", "�߾ӵ�����", "��1�л�ȸ��", "���п�", "���ǰ�", "�ٺ�ġ��"};
	static private final int[][] LOCATION = { {461, 525}, {485, 405}, {539, 291}, {660, 346}, {733, 406}, {875, 326},
			{801, 262}, {709, 213}, {802, 175}, {651, 458}, {589, 436}, {340, 281}, {255, 282}, {239, 256}, {228, 192}, {152, 216}, {276, 147}
	};
	static private Group lineGroup; // ���
	static private TextField pathTf = new TextField(); // ��� �ؽ�Ʈ
	static private TextField meterTf = new TextField(); // ���� �Ÿ� �ؽ�Ʈ
	static private TextField timeTf = new TextField(); // ���� �ð� �ؽ�Ʈ
	
	static private String[] pathNode;
	static private Pane mainPane;
	
	static private int count = 0; // ��� �˻� Ƚ��
	static private Color color; // ��� ����
	//no-arg ������
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
		// �� ��庰 LOCATION ���� ��, ����� �޼����Դϴ�.
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
		
		startCbo.setValue("����� ����");
		endCbo.setValue("������ ����");
		
		Button btOK = new Button("OK");
		lineGroup = new Group();

		btOK.setOnAction( e-> {
			int start = convert(startCbo.getValue()); // comboBox���� ������ ������� �迭�� ������ȣ�� �ٲ��ݴϴ�.
			int end = convert(endCbo.getValue()); // comboBox���� ������ �������� �迭�� ������ȣ�� �ٲ��ݴϴ�.
			
			Path path = new Path(start,end); // �Է��� ������� �������� �������� Path�� �����մϴ�.

			try {
				path.calPath(); // ��� ���
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			pathNode = path.getPathNode().split(" ", 0); // ���� ���(String)�� ������ ���� �迭�� ����ϴ�.
			changeColor(); // ��� ������ �����մϴ�.
			
			// ķ�۽������� �׸� Line��ü�� pathNode�� ���� �����մϴ�.
			for(int i = 1; i < pathNode.length; i++) {
				System.out.println(pathNode[i-1]);
				Line tmpLine = new Line(LOCATION[Integer.parseInt(pathNode[i-1])-1][0], LOCATION[Integer.parseInt(pathNode[i-1])-1][1], LOCATION[Integer.parseInt(pathNode[i])-1][0], LOCATION[Integer.parseInt(pathNode[i])-1][1]);
				tmpLine.setStrokeWidth(3);
				tmpLine.setStroke(color);
				lineGroup.getChildren().add(tmpLine);
			}

			// �ǹ� �������ڷ� �� ��θ� �ǹ������� �ٲߴϴ�.
			String pathInfo = "";
			for(int i = 0; i < pathNode.length; i++) {
				pathInfo += NAMESPOT[Integer.parseInt(pathNode[i])-1];
				if(i == pathNode.length-1)
					break;
				pathInfo += " -> ";
			}
			int minute = path.getTime() / 60;
			int second = path.getTime() % 60;
			
			pathTf.setText("��� : " + pathInfo + " (�� " + path.getNodeCount() + "�� ����)");
			meterTf.setText("�Ÿ� : " + path.getMeter() + "m");
			timeTf.setText("���� �ҿ�ð� : " + minute + "�� " + second + "��");
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

		Button btClear = new Button("���ΰ�ħ");
		btClear.setOnAction(e->{
			lineGroup = null;
			start(new Stage()); // �� â ����
			priStage.close(); // ���� â �ݱ�
		}
				);
		//��ο� �Ÿ��� ����� label ����

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
		int result = -1; // �� ã���� ���, -1�� ��ȯ�մϴ�.

		// �ǹ��̸����� ã���ϴ�.
		for(int i = 0; i < NODE; i++) {
			if(spot.equals(NAMESPOT[i])) {
				result = i+1;
				break;
			}
		}
		
		// ������ �������� �ʾ��� ���, �⺻ ������� �������� �����մϴ�.
		if(spot.equals("����� ����")) {
			result = 1;
			
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("����");
			alert.setHeaderText("����� �̼���");
			alert.setContentText("�⺻ ������� '����'���� ���õ� ����� �������ϴ�.");

			alert.showAndWait();
		}
		if(spot.equals("������ ����")) {
			result = 9;
			
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("����");
			alert.setHeaderText("������ �̼���");
			alert.setContentText("�⺻ �������� '�Ĺ�'���� ���õ� ����� �������ϴ�.");

			alert.showAndWait();
		}
		
		return result;
	}
}

