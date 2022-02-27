package com.example.todolist.entity;

import java.sql.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
//このクラスがエンティティであることを示すアノテーション
@Table(name = "todo")
//このエンティティに対応づけるテーブルを指定するアノテーション
//→これによってTodoクラスへの操作は自動的にtodoテーブルのレコード操作となる
@Data
//ロンボックのアノテーション
//→これによってgetter,setter,toStringメソッドが自動生成される
public class Todo {



@Id
//テーブルの主キーに対応する変数（この場合はid）であることを示すアノテーション
@GeneratedValue(strategy = GenerationType.IDENTITY)
//主キーが自動採番されることを示すアノテーション
//PostgreSQLで主キーをSERIAL型とした場合はstrategyを上記のように指定する
@Column(name = "id")
//変数に対応するテーブルの列を指定するアノテーション
private Integer id;


@Column(name = "title")
private String title;

@Column(name = "importance")
private Integer importance;

@Column(name = "urgency")
private Integer urgency;

@Column(name = "deadline")
private Date deadline;

@Column(name = "done")
private String done;


}
