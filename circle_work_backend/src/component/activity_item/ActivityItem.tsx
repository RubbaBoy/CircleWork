import React, {useContext, useState} from 'react'
import './ActivityItem.scss'
import {Goal, GoalCategory} from "../../logic/objects";
import {convertColor} from "../../logic/utilities";
import {Card} from "react-bootstrap";
import CategoriesContext, {getCategory} from "../../logic/Category";
import {reformatDate} from "../goals/Goals";

interface ActivityItemProps {
    title: string
    showDate: boolean
    showCategory: boolean
    goal: Goal
    onApprove: (e: React.MouseEvent<HTMLElement, MouseEvent>, goal: Goal, category: GoalCategory) => void
    onClick: (e: React.MouseEvent<HTMLElement, MouseEvent>, goal: Goal, category: GoalCategory) => void
    showApprove: (goal: Goal) => boolean
}

export const ActivityItem = (props: ActivityItemProps) => {
    const [hovering, setHovering] = useState<boolean>(false)
    const categories = useContext(CategoriesContext)

    let goal = props.goal;
    let category = getCategory(categories, goal.categoryId);

    return (
        <Card className={"ActivityItem"} style={{...(hovering ? {boxShadow: `0 0px 10px #${convertColor(category.color)}`} : null)}} onMouseEnter={() => setHovering(true)} onMouseLeave={() => setHovering(false)} onClick={(e) => props.onClick(e, goal, category)}>
            <Card.Body className={"body"}>
                <div className={"left"}>
                    <div className="title-line">
                        <span className="title">{props.title}</span>
                        {props.showDate && <span className="date">{reformatDate(goal.due_date)}</span>}
                        {props.showCategory && <span className="category">{category.name}</span>}
                    </div>
                    <p className="body">{goal.goal_body}</p>
                </div>
                {props.showApprove(goal) &&
                    <div className={"right"} onClick={e => props.onApprove(e, goal, category)}>
                        <svg xmlns="http://www.w3.org/2000/svg" className="approve" height="24px" viewBox="0 0 24 24" width="24px" fill="#000000">
                            <path d="M0 0h24v24H0V0z" fill="none"/>
                            <path d="M9 16.2L4.8 12l-1.4 1.4L9 19 21 7l-1.4-1.4L9 16.2z"/>
                        </svg>
                    </div>}
            </Card.Body>
        </Card>
    )
}

ActivityItem.defaultProps = {
    showDate: true,
    showCategory: true
}
