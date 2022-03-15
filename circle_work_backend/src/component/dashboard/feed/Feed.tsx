import React, {useEffect, useState} from 'react'
import './Feed.scss'
import {Goal} from "../../../logic/objects";
import {Card, Row} from "react-bootstrap";
import {fetchAuthed} from "../../../logic/request-helper";
import {SimpleToast} from "../../toast/Toast";
import {ActivityItem} from "../../activity_item/ActivityItem";
import {useNavigate} from "react-router";

export const Feed = () => {
    const navigate = useNavigate()
    const [items, setItems] = useState<Goal[]>([])
    const [error, setError] = useState<string | undefined>()
    const [title, setTitle] = useState<string | undefined>()
    const [showApprove, setShowApprove] = useState<boolean>(false)

    useEffect(() => {
        fetchAuthed('circles/feed', {method: 'GET'}).then(async res => {
            let json = await res.json()

            if (res.status != 200) {
                console.log('Bad request status ' + res.status)
                console.log(json)
                setError('The feed could not be loaded')
                return
            }

            setError(undefined)

            let items: Goal[] = json
            console.log('items =');
            console.log(items);

            setItems(items)
        })
    }, [])

    function approve(goal: Goal) {
        fetchAuthed('goals/approve', {method: 'POST', body: JSON.stringify({
                'goal_id': goal.id,
            })}).then(async res => {
            let json = await res.json()

            if (res.status != 200) {
                console.log('Bad request status ' + res.status)
                console.log(json)
                return
            }

            let approvedCount = json['approved']
            // TODO: Do anything with this?

            setTitle(goal.goal_name)
            setShowApprove(true)
        })
    }

    function navigateCategory(e: React.MouseEvent<HTMLElement, MouseEvent>, id: number) {
        navigate('/resources/' + id)
    }

    function showFeed() {
        if (items.length == 0) {
            return <p className="empty-feed">It looks like the feed is empty</p>
        }

        return items.map(goal =>
            <ActivityItem goal={goal} title={`${goal.owner_name} completed: ${goal.goal_name}`} showApprove={() => true} onApprove={(e, group) => approve(group)} onClick={(e, goal, category) => navigateCategory(e, category.id)}/>)
    }

    return <Row className="Feed">
        <SimpleToast title={title ?? ''} color={'#10cc10'} body={'Thank you for contributing!'} show={showApprove} onHide={() => setShowApprove(false)}/>
        <h5 className="mt-3">Activity Feed</h5>
        <ul className="feed">
            {error != undefined ? <p className="error">{error}</p> : showFeed()}
        </ul>
    </Row>
}
