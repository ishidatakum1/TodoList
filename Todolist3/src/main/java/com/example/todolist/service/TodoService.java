package com.example.todolist.service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.example.todolist.entity.Todo;
import com.example.todolist.form.TodoData;
import com.example.todolist.form.TodoQuery;
import com.example.todolist.repository.TodoRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TodoService {
	private final TodoRepository todoRepository;
	
//	BindingResultクラスはインターフェースだが、その実装クラスのDataBinderのgetBindingResultメソッドによって、バリデーションの結果を保持するBindingResultインスタンスが生成される
	public boolean isValid(TodoData todoData, BindingResult result) {
		boolean ans = true;
		
//		件名が全角スペースだけで構成されていたらエラー
		String title = todoData.getTitle();
		if (title != null && !title.equals("")) {
//			→titleがnullでも空文字でもない場合
			boolean isAllDoubleSpace = true;
//			charAt(i)メソッドは文字列の先頭を0番目として引数で指定したi番目の文字を抜き出す
//			for文でiを文字列の長さ分指定してiを引数に持つcharAtメソッドで文字列中に全角スペースが含まれるかを探す
			for (int i = 0; i < title.length(); i++) {
				if (title.charAt(i) != '　') {
					isAllDoubleSpace = false;
					break;
				}
			}
			if (isAllDoubleSpace) {
				
				FieldError fieldError = new FieldError(result.getObjectName(), "title", "件名が全角スペースです");
				result.addError(fieldError);
				ans = false;
			}
		}
		
//		期限が過去日付ならエラー
		String deadline = todoData.getDeadline();
		if (!deadline.equals("")) {
			LocalDate today = LocalDate.now();
			LocalDate deadlineDate = null;
			try {
				deadlineDate = LocalDate.parse(deadline);
				if(deadlineDate.isBefore(today)) {
					FieldError fieldError = new FieldError(result.getObjectName(), "deadline", "期限を設定するときは今日以降にしてください");
					result.addError(fieldError);
					ans = false;
				}
			} catch (DateTimeException e) {
				FieldError fieldError = new FieldError(result.getObjectName(), "deadline", "期限を設定するときはyyyy-mm-dd形式で入力してください");
				result.addError(fieldError);
				ans = false;
			}
		}
		return ans;
	}
	
	
	
	public boolean isValid(TodoQuery todoQuery, BindingResult result) {
		boolean ans = true;
		String date = todoQuery.getDeadlineFrom();
		if(!date.equals("")) {
			try {
				LocalDate.parse(date);
			} catch (DateTimeException e) {
				FieldError fieldError = new FieldError(result.getObjectName(), "deadlineFrom", "期限：開始を入力するときはyyyy-mm-dd形式で入力してください");
				result.addError(fieldError);
				ans = false;
			}
		}
		date = todoQuery.getDeadlineTo();
		if (!date.equals("")) {
			try {
				LocalDate.parse(date);
			} catch (DateTimeException e) {
				FieldError fieldError = new FieldError(result.getObjectName(), "deadlineTo", "期限：終了を入力するときはyyyy-mm-dd形式で入力してください");
				result.addError(fieldError);
				ans = false;
			}
		}
		return ans;
	}
	
	
	
	public List<Todo> doQuery(TodoQuery todoQuery){
		List<Todo> todoList = null;
		if (todoQuery.getTitle().length() > 0) {
			todoList = todoRepository.findByTitleLike("%" + todoQuery.getTitle() + "%");
		} else if (todoQuery.getImportance() != null && todoQuery.getImportance() != -1) {
			todoList = todoRepository.findByImportance(todoQuery.getImportance());
		} else if (todoQuery.getUrgency() != null && todoQuery.getUrgency() != -1) {
			todoList = todoRepository.findByUrgency(todoQuery.getUrgency());
		} else if (!todoQuery.getDeadlineFrom().equals("") && todoQuery.getDeadlineTo().equals("")) {
			todoList = todoRepository.findByDeadlineGreaterThanEqualOrderByDeadlineAsc(com.example.todolist.common.Utils.str2date(todoQuery.getDeadlineFrom()));
		} else if (todoQuery.getDeadlineFrom().equals("") && !todoQuery.getDeadlineTo().equals("")) {
			todoList = todoRepository.findByDeadlineLessThanEqualOrderByDeadlineAsc(com.example.todolist.common.Utils.str2date(todoQuery.getDeadlineTo()));
		} else if (!todoQuery.getDeadlineFrom().equals("") && !todoQuery.getDeadlineTo().equals("")) {
			todoList = todoRepository.findByDeadlineBetweenOrderByDeadlineAsc(com.example.todolist.common.Utils.str2date(todoQuery.getDeadlineFrom()), com.example.todolist.common.Utils.str2date(todoQuery.getDeadlineTo()));
		} else if (todoQuery.getDone() != null && todoQuery.getDone().equals("Y")) {
			todoList = todoRepository.findByDone("Y");
		} else {
			todoList = todoRepository.findAll();
		}
		return todoList;
	}
	
}
