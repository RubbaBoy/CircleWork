import React, {useState, useEffect, Fragment} from 'react';
import {GoalCategory} from "../../logic/objects";
import {fetchApi} from "../../logic/request-helper";
import {listCategories} from "../dashboard/Dashboard";
import {navSideButton} from "../home/Home";
import {Card, Col, Row} from "react-bootstrap";
import {someBody} from "../stuff";
import {useNavigate} from "react-router";


export const Categories = () => {
    const navigate = useNavigate()

    const[categories, setCategories] = useState<GoalCategory[]>([])

    useEffect(() =>{
        listCategories().then(categories => setCategories(categories))
    }, [])

    return(
        <Fragment>
            <div className="side-buttons">
                {navSideButton('Dashboard', '/dashboard')}
            </div>

            <Row className="Goals mx-0">
                {someBody(
                    <div className="sidebar">
                        <div className="bottom-round"></div>
                    </div>,
                    <Fragment>
                        <Row xs={12} className="first-row">
                                <h4>Categories</h4>
                                <p>here are our default categories :)</p>

                                <Row xs={12} className="list-row">
                                    {categories.map(category => {
                                        return <Col xs={3}>
                                            <Card className="categoriessoemthing" onClick={() => navigate('resources/' + category.id)}>
                                                <Card.Body>
                                                    <Card.Title className="m-0">
                                                        {category.name}
                                                    </Card.Title>
                                                    {category.description}
                                                </Card.Body>
                                            </Card>
                                        </Col>;
                                    })}
                                </Row>
                            </Row>

                    </Fragment>
                )}
            </Row>
        </Fragment>
    );

}