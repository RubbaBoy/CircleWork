import React, {createRef, Fragment, useEffect, useState} from 'react'
import './Dashboard.scss'
import {Button, Col, Form, Modal, Row} from "react-bootstrap";
import {someBody} from "../stuff";
import {hrefSideButton, navSideButton} from "../home/Home";
import {fetchAuthed} from "../../logic/request-helper";
import {GoalCategory} from "../../logic/objects";
import {SimpleToast} from "../toast/Toast";
import {Feed} from "./feed/Feed";
import {GlobalStats} from "./global_stats/GlobalStats";
import {CircleStats} from "./circle_stats/CircleStats";
import {WeekView} from "./week_view/WeekView";
import {useNavigate} from "react-router";

interface AddGoalProps {
    show: boolean
    onHide: () => void
}

export function listCategories(onError?: (message: string | undefined) => void): Promise<GoalCategory[]> {
    return fetchAuthed('category/list', {
        method: 'GET'
    }).then(async res => {
        let json = await res.json()

        if (res.status != 200) {
            console.log('Bad request status ' + res.status)
            console.log(json)
            onError?.('There was an error listing categories')
            return []
        }

        onError?.(undefined)

        let categories: GoalCategory[] = json
        console.log('categories =');
        console.log(categories);

        return categories

        // setCategories(categories)
    })
}

const AddGoalModal = (props: AddGoalProps) => {
    let nameRef = createRef<HTMLInputElement>()
    let descRef = createRef<HTMLTextAreaElement>()
    let privateRef = createRef<HTMLInputElement>()
    let categoryRef = createRef<HTMLSelectElement>()
    const [showGoalToast, setShowGoalToast] = useState(false);
    const [name, setName] = useState<string>();

    const [error, setError] = useState<string | undefined>()
    const [categories, setCategories] = useState<GoalCategory[]>([])

    useEffect(() => {
        listCategories(setError)
            .then(categories => setCategories(categories))
    }, [])

    function onHide() {
        props.onHide()
    }

    function addGoal() {
        let category = categoryRef.current?.value
        if (category == '-1') {
            setError('Please select a category')
            return
        }

        setName(nameRef.current?.value ?? '')

        setError(undefined)

        fetchAuthed('goal/add', {
            method: 'POST',
            body: JSON.stringify({
                'private': privateRef.current?.checked,
                'goalname': nameRef.current?.value,
                'goalbody': descRef.current?.value,
                'category': categoryRef.current?.value
            })
        }).then(async res => {
            let json = await res.json()

            if (res.status != 200) {
                console.log('Bad request status ' + res.status)
                console.log(json)
                setError('There was an error adding your goal :(')
                return
            }

            setError(undefined)

            let goalId = json['goal_id']
            console.log('created goal ' + goalId);

            props.onHide()
            setShowGoalToast(true)
        })
    }

    return (
        <Fragment>
            <SimpleToast title={`Added "${name}"`} body="Added your goal, make sure to tell your circle once you complete it!" show={showGoalToast} onHide={() => setShowGoalToast(false)} color={'77DD77'}/>
            <Modal
                {...props}
                size="lg"
                aria-labelledby="contained-modal-title-vcenter"
                centered
            >
                <Modal.Header closeButton>
                    <Modal.Title id="contained-modal-title-vcenter">
                        Add A Goal
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <h5>This should be</h5>
                    <Form>
                        <Form.Group className="mb-3" controlId="formBasicEmail">
                            <Form.Label>Goal name</Form.Label>
                            <Form.Control ref={nameRef} type="text" placeholder="e.g. Get a B in calc"/>
                            <Form.Text className="text-muted">
                                Something short, you can elaborate below
                            </Form.Text>
                        </Form.Group>

                        <Form.Group className="mb-3" controlId="formBasicPassword">
                            <Form.Label>Goal description</Form.Label>
                            <Form.Control ref={descRef} as="textarea" placeholder="What your goal actually is"/>
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Goal category</Form.Label>
                            <Form.Select ref={categoryRef}>
                                <option value='-1'>Category</option>
                                {categories.map(category => <option value={`${category.id}`}>{category.name}</option>)}
                            </Form.Select>
                        </Form.Group>
                        <Form.Group className="mb-3" controlId="formBasicCheckbox">
                            <Form.Check ref={privateRef} type="checkbox" label="Is this a private goal? (Not counted towards circles)"/>
                        </Form.Group>
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    {error != undefined && <p className="error me-auto">{error}</p>}
                    <Button onClick={() => addGoal()}>Add</Button>
                    <Button variant="light" onClick={() => onHide()}>Close</Button>
                </Modal.Footer>
            </Modal>
        </Fragment>
    );
}

export const Dashboard = () => {
    const navigate = useNavigate()
    const [addingGoal, setAddGoal] = useState(false);

    return (
        <Fragment>
            <AddGoalModal show={addingGoal} onHide={() => setAddGoal(false)}/>

            <div className="side-buttons">
                {navSideButton('Helpful Resources', '/resources')}
            </div>

            <Row className="Dashboard mx-0">
                {someBody(
                    <div className="sidebar upside-down">
                        <div className="bottom-round"></div>

                        <div className="vert-text">
                            <span>R</span>
                            <span>A</span>
                            <span>O</span>
                            <span>B</span>
                            <span>H</span>
                            <span>S</span>
                            <span>A</span>
                            <span>D</span>
                        </div>
                    </div>,
                    <Fragment>
                        <WeekView/>

                        <Row className="button-row mt-3">
                            <Button className="add" onClick={() => setAddGoal(true)}>Add Goal</Button>
                            <Button className="view" variant="light" onClick={() => navigate('/goals')}>View Goals</Button>
                        </Row>

                        <Feed/>

                        <Row>
                            <Col xs={6}>
                                <CircleStats/>
                            </Col>
                            <Col xs={6}>
                                <GlobalStats/>
                            </Col>
                        </Row>
                    </Fragment>
                )}
            </Row>
        </Fragment>
    )
}
