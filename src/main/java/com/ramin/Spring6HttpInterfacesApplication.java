package com.ramin;

import java.util.List;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@SpringBootApplication
public class Spring6HttpInterfacesApplication {

	public static void main(String[] args) {
		SpringApplication.run(Spring6HttpInterfacesApplication.class, args);
	}

	@Bean
	TodoClient todoClient() {
		WebClient webClient = WebClient.builder()
				.baseUrl("https://jsonplaceholder.typicode.com/")
				.build();
		HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(new WebClientAdapter(webClient))
				//.addCustomResolver(((argument, parameter, requestValues) -> {parameter.hasParameterAnnotation()}))
				.build();
		return factory.createClient(TodoClient.class);
	}

	@Bean
	ApplicationRunner applicationRunner(TodoClient todoClient) {
		return args -> {
			System.out.println(todoClient.todos());
			Todo laundry = todoClient.create(new Todo(null, "laundry", false, 1L));
			System.out.println(laundry);
			System.out.println(todoClient.get(laundry.id()).getBody());
		};
	}

}

@HttpExchange("/todos")
interface TodoClient {

	@GetExchange
	List<Todo> todos();

	@PostExchange
	Todo create(@RequestBody Todo todo);

	@GetExchange("/{todoId}")
	ResponseEntity<Todo> get(@PathVariable("todoId") Long id);

}

record Todo(Long id, String title, boolean completed, Long userId) {}
