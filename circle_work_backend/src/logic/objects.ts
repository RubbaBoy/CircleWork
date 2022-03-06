import internal from "stream";

export interface Goal {
    id: number
    owner_id: number
    owner_name: string
    is_private: boolean
    goal_name: string
    goal_body: string
    approval_count: number
    categoryId: number
    due_date: string
}

export interface Circle {
    id: number
    name: string
    color: number
    team_count: number
    monthly_donation: number
    total_donation: number
    tasks_started: number
    tasks_completely: number
}

export interface GoalCategory{
    id: number
    name: string
    description: string
    color: number
}

export interface Charity{
    id: number
    name: string
    image: string
    link: string
    raised_monthly: number
    raised_total: number
}

export interface Payment{
    id: number
    user_id: number
    amount: number
}

export interface CategoryResources{
    id: number
    category: number
    title: string
    link: string
    type: number
}

export interface DonationCounts {
    total: number
    month: number
}
