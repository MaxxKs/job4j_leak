package ru.job4j.gc.leak;

import ru.job4j.gc.leak.models.Post;

import java.util.Random;
import java.util.Scanner;

public class Menu {

    private static final int ADD_POST = 1;
    private static final int ADD_MANY_POST = 2;
    private static final int SHOW_ALL_POSTS = 3;
    private static final int DELETE_POST = 4;

    private static final String SELECT = "Выберите меню";
    private static final String COUNT = "Выберите количество создаваемых постов";
    private static final String TEXT_OF_POST = "Введите текст";
    private static final String EXIT = "Конец работы";

    private static final String MENU = """
                Введите 1 для создание поста.
                Введите 2, чтобы создать определенное количество постов.
                Введите 3, чтобы показать все посты.
                Введите 4, чтобы удалить все посты.
                Введите любое другое число для выхода.
            """;

    public static void main(String[] args) {
        Random random = new Random();
        UserGenerator userGenerator = new UserGenerator(random);
        CommentGenerator commentGenerator = new CommentGenerator(random, userGenerator);
        Scanner scanner = new Scanner(System.in);
        PostStore postStore = new PostStore();
        Menu menu = new Menu();
        menu.start(commentGenerator, scanner, userGenerator, postStore);
    }

    private void start(CommentGenerator commentGenerator, Scanner scanner, UserGenerator userGenerator, PostStore postStore) {
        boolean run = true;
        userGenerator.generate();
        while (run) {
            System.out.println(MENU);
            System.out.println(SELECT);
            int userChoice = Integer.parseInt(scanner.nextLine());
            System.out.println(userChoice);
            if (ADD_POST == userChoice) {
                System.out.println(TEXT_OF_POST);
                String text = scanner.nextLine();
                Post post = createPost(commentGenerator, postStore, text);
                System.out.println("Generate: " + post.getId());
            } else if (ADD_MANY_POST == userChoice) {
                System.out.println(TEXT_OF_POST);
                String text = scanner.nextLine();
                System.out.println(COUNT);
                String count = scanner.nextLine();
                System.out.printf("Память до создания: %.2f Mb%n", memUsage());
                int total = Integer.parseInt(count);
                for (int i = 0; i < total; i++) {
                    System.out.printf("\rGenerate %.2f%% %.2fMb",
                            ((double) i / total) * 100, memUsage());
                    createPost(commentGenerator, postStore, text);
                }
                System.out.println();
                System.out.printf("Память после создания: %.2f Mb%n", memUsage());
            } else if (SHOW_ALL_POSTS == userChoice) {
                System.out.println(postStore.getPosts());
            } else if (DELETE_POST == userChoice) {
                System.out.println("Удаление всех постов ...");
                postStore.removeAll();
            } else {
                run = false;
                System.out.println(EXIT);
            }
        }
    }

    private double memUsage() {
        var rt = Runtime.getRuntime();
        var totalMem = rt.totalMemory();
        var freeMem = rt.freeMemory();
        var usedMem = totalMem - freeMem;
        return (double) usedMem / 1024 / 1024;
    }

    private Post createPost(CommentGenerator commentGenerator,
                                   PostStore postStore, String text) {
        commentGenerator.generate();
        var post = new Post();
        post.setText(text);
        post.setComments(commentGenerator.getComments());
        postStore.add(post);
        return post;
    }
}