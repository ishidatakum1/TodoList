package com.example.todolist.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.todolist.entity.Todo;
import com.example.todolist.form.TodoData;
import com.example.todolist.form.TodoQuery;
import com.example.todolist.repository.TodoRepository;
import com.example.todolist.service.TodoService;

import lombok.AllArgsConstructor;

@Controller
//全フィールドへ値をセットするコンストラクタを自動生成するロンボックのアノテーション
@AllArgsConstructor
public class TodoListController {
//コンストラクタが一つしかない場合は@Autowired(インスタンスの自動生成)を省略できる（Springのルール）
	private final TodoRepository todoRepository;
	private final TodoService todoService;
	private final HttpSession session;
	
	
//	ToDo一覧表示
	@GetMapping("/todo")
	public ModelAndView showTodoList(ModelAndView mv) {
		mv.setViewName("todoList");
//		TodoRepositoryクラスはJPAを継承しているため、自動実装されたSQL操作メソッドが多数あり、findAll（全レコードの取得）もその一つ
		List<Todo> todoList = todoRepository.findAll();
		mv.addObject("todoList",todoList);
		mv.addObject("todoQuery", new TodoQuery());
		return mv;
		}
	
	
	
//	ToDo入力フォームの表示
	
//	①ToDo一覧画面(todoList.html)で『新規追加』がクリックされた時
	@PostMapping("/todo/create/form")
	public ModelAndView createTodo(ModelAndView mv) {
		mv.setViewName("todoForm");
		mv.addObject("todoData", new TodoData());
		session.setAttribute("mode", "create");
		return mv;
	}
	
//	②ToDo入力画面(todoForm.html)で『登録』がクリックされた時
	@PostMapping("/todo/create/do")
	public String createTodo(@ModelAttribute @Validated TodoData todoData, BindingResult result, ModelAndView mv) {
//		エラーチェック
		boolean isValid = todoService.isValid(todoData,result);
		if (!result.hasErrors() && isValid) {
//			エラーなし
			Todo todo = todoData.toEntity();
			todoRepository.saveAndFlush(todo);
			return "redirect:/todo";
		} else {
//			エラーあり
			mv.setViewName("todoForm");
			return "todoForm";
		}
	}
		
//	③ToDo入力画面(todoForm.html)で『キャンセル』がクリックされた時
	@PostMapping("/todo/cancel")
	public String cancel() {
		return "redirect:/todo";
	}
	
	
	
//	todoテーブルの検索
	@GetMapping("/todo/{id}")
	public ModelAndView todoById(@PathVariable(name = "id")int id, ModelAndView mv) {
		mv.setViewName("todoForm");
		Todo todo = todoRepository.findById(id).get();
		mv.addObject("todoData", todo);
		session.setAttribute("mode", "update");
		return mv;
	}
	
	
	
	@PostMapping("/todo/update")
	public String updateTodo(@ModelAttribute @Validated TodoData todoData, BindingResult result, Model model) {
		boolean isValid = todoService.isValid(todoData,result);
		if (!result.hasErrors() && isValid) {
//			エラーなし
			Todo todo = todoData.toEntity();
			todoRepository.saveAndFlush(todo);
			return "redirect:/todo";
		} else {
//			エラーあり
			return "todoForm";
		}
	}
	
	
	
	@PostMapping("/todo/delete")
	public String deleteTodo(@ModelAttribute TodoData todoData) {
		todoRepository.deleteById(todoData.getId());
		return "redirect:/todo";
	}
	
	
	
	@PostMapping("/todo/query")
	 public ModelAndView queryTodo(@ModelAttribute TodoQuery todoQuery, BindingResult result, ModelAndView mv) {
		mv.setViewName("todoList");
		List<Todo> todoList = null;
		if(todoService.isValid(todoQuery, result)) {
			todoList = todoService.doQuery(todoQuery);
		}
		mv.addObject("todoList", todoList);
		return mv;
	}
	
}
