create table if not exists public.users (
    id text primary key,
    email text not null unique,
    password text not null,
    role text not null default 'USER',
    tokens integer not null default 0
);

create table if not exists public.tasks (
    id text primary key,
    text text not null,
    status text not null default 'Pending',
    category text not null default 'Personal',
    user_id text not null,
    constraint fk_tasks_user foreign key (user_id) references public.users(id) on delete cascade
);

create table if not exists public.milestones (
    id text primary key,
    name text not null,
    progress integer not null default 0,
    celebrated boolean not null default false,
    user_id text not null,
    constraint fk_milestones_user foreign key (user_id) references public.users(id) on delete cascade
);

create table if not exists public.important_tasks (
    id text primary key,
    task_name text not null,
    event_time timestamp not null,
    user_email text not null,
    user_id text not null,
    processed boolean not null default false,
    constraint fk_important_tasks_user foreign key (user_id) references public.users(id) on delete cascade
);

create table if not exists public.user_queries (
    id text primary key,
    user_email text not null,
    message text not null,
    admin_reply text,
    timestamp timestamp not null default now(),
    resolved boolean not null default false
);

create table if not exists public.user_unlocked_rewards (
    user_id text not null,
    reward_key text not null,
    constraint fk_unlocked_rewards_user foreign key (user_id) references public.users(id) on delete cascade
);

create table if not exists public.user_reward_unlocked_dates (
    user_id text not null,
    reward_key text not null,
    unlocked_date text,
    constraint fk_reward_dates_user foreign key (user_id) references public.users(id) on delete cascade,
    constraint pk_reward_dates primary key (user_id, reward_key)
);

create index if not exists idx_tasks_user_id on public.tasks(user_id);
create index if not exists idx_tasks_user_status on public.tasks(user_id, status);
create index if not exists idx_milestones_user_id on public.milestones(user_id);
create index if not exists idx_important_tasks_user_id on public.important_tasks(user_id);
create index if not exists idx_important_tasks_processed on public.important_tasks(processed);
create index if not exists idx_user_queries_user_email on public.user_queries(user_email);

do $$
begin
    if not exists (
        select 1
        from pg_publication_tables
        where pubname = 'supabase_realtime'
          and schemaname = 'public'
          and tablename = 'tasks'
    ) then
        alter publication supabase_realtime add table public.tasks;
    end if;

    if not exists (
        select 1
        from pg_publication_tables
        where pubname = 'supabase_realtime'
          and schemaname = 'public'
          and tablename = 'milestones'
    ) then
        alter publication supabase_realtime add table public.milestones;
    end if;
end $$;
