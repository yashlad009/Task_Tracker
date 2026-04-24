-- Run this in the Supabase SQL editor.
-- This script assumes your Spring backend is the only database client and
-- that you are not querying these tables directly from the browser with the
-- Supabase Data API.

begin;

alter table if exists public.users enable row level security;
alter table if exists public.tasks enable row level security;
alter table if exists public.milestones enable row level security;
alter table if exists public.important_tasks enable row level security;
alter table if exists public.user_queries enable row level security;
alter table if exists public.user_reward_unlock_dates enable row level security;
alter table if exists public.user_unlocked_rewards enable row level security;

revoke all on table public.users from anon, authenticated;
revoke all on table public.tasks from anon, authenticated;
revoke all on table public.milestones from anon, authenticated;
revoke all on table public.important_tasks from anon, authenticated;
revoke all on table public.user_queries from anon, authenticated;
revoke all on table public.user_reward_unlock_dates from anon, authenticated;
revoke all on table public.user_unlocked_rewards from anon, authenticated;

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'user_unlocked_rewards_pkey'
    ) then
        alter table public.user_unlocked_rewards
            add constraint user_unlocked_rewards_pkey primary key (user_id, reward_key);
    end if;
end $$;

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'user_reward_unlock_dates_pkey'
    ) then
        alter table public.user_reward_unlock_dates
            add constraint user_reward_unlock_dates_pkey primary key (user_id, reward_key);
    end if;
end $$;

commit;
