CREATE TABLE public.user (
      id serial PRIMARY KEY ,
      username VARCHAR(255) NOT NULL,
      password VARCHAR(255) NOT NULL,
      CONSTRAINT UK_user_username UNIQUE (username)
);

CREATE TABLE public.user_roles (
    user_id INTEGER NOT NULL,
    roles VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, roles),
    CONSTRAINT FK_user_roles_user FOREIGN KEY (user_id) REFERENCES public.user(id)
);
