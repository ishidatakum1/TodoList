package com.example.todolist.form;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.example.todolist.entity.Todo;
import lombok.Data;


@Data
public class TodoData {
	private Integer id;
	
//	空白でないことのバリデーション
	@NotBlank(message="件名を入力してください")
	private String title;
	
//	Nullでないことのバリデーション
	@NotNull(message="重要度を選択してください")
	private Integer importance;
	
//	指定値より大きいことのバリデーション
	@Min(value=0,message="緊急度を選択してください")
	private Integer urgency;
	
	private String deadline;
	private String done;
	
	
//	入力データからentityを生成して返す
	public Todo toEntity() {
		Todo todo = new Todo();
		
		todo.setId(id);
		todo.setTitle(title);
		todo.setImportance(importance);
		todo.setUrgency(urgency);
		todo.setDone(done);
		
//		期限データの書式を設定
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
//		try-catch構文では変数はスコープを意識してtryブロックの外で宣言
		long ms;
		try {
//			String型で入力された変数deadlineをメソッドチェーンを使って以下の順に変換、代入する
//			①SimpleDateFormatクラスの継承元であるDateFormatクラスのparseメソッドを使って上記のフォーマット形式でdate型インスタンス変数に変換
//			②DateクラスのgetTimeメソッドを使ってlong型に変換し、変数msに代入
			ms = sdFormat.parse(deadline).getTime();
//			③long値を引数に持つjava.sql.Dateインスタンスを生成し、変数deadlineに代入
			todo.setDeadline(new Date(ms));
		} catch (ParseException e) {
			todo.setDeadline(null);
		}
		
		return todo;
	}
	
}
