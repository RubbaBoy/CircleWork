import React, {useEffect, useState} from 'react'
import './Feed.scss'
import {Goal} from "../../../logic/objects";
import {Card, Row} from "react-bootstrap";
import {fetchAuthed} from "../../../logic/request-helper";
import {SimpleToast} from "../../toast/Toast";

export const Feed = () => {
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

    function showFeed() {
        if (items.length == 0) {
            return <p className="empty-feed">It looks like the feed is empty</p>
        }

        return items.map(goal => {
            return <Card className={"item"}>
                <Card.Body className={"body"}>
                    <div className={"left"}>
                        <span className="title">{goal.owner_name} completed a task!</span>
                        <p className="body">{goal.owner_name} completed: {goal.goal_name}</p>
                    </div>
                    <div className={"right"}>
                        <svg xmlns="http://www.w3.org/2000/svg" className="approve" height="24px" viewBox="0 0 24 24" width="24px" fill="#000000" onClick={() => approve(goal)}>
                            <path d="M0 0h24v24H0V0z" fill="none"/>
                            <path d="M9 16.2L4.8 12l-1.4 1.4L9 19 21 7l-1.4-1.4L9 16.2z"/>
                        </svg>
                    </div>
                </Card.Body>
            </Card>
        })
    }

    return <Row className="Feed">
        <SimpleToast title={title ?? ''} color={'#10cc10'} body={'Thank you for contributing!'} show={showApprove} onHide={() => setShowApprove(false)}/>
        <h5 className="mt-3">Activity Feed</h5>
        <ul className="feed">
            {error != undefined ? <p className="error">{error}</p> : showFeed()}
        </ul>
    </Row>
}
