CREATE TABLE  IF NOT EXISTS circles(
    id SERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(64),
    color INT DEFAULT 0,
    team_count INT DEFAULT 1,
    tasks_started INT DEFAULT 0,
    tasks_completed INT DEFAULT 0,
    raised_monthly INT DEFAULT 0,
    raised_total INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS  goal_categories (
    id SERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(64),
    description VARCHAR(4096),
    color INT
);

CREATE TABLE IF NOT EXISTS  users (
    id SERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(64),
    password VARCHAR(256),
    balance INT,
    tasks_started INT DEFAULT 0,
    tasks_completed INT DEFAULT 0,
    circle_id INT NOT NULL REFERENCES circles(id)
);

CREATE TABLE IF NOT EXISTS goals (
    id SERIAL PRIMARY KEY NOT NULL,
    owner INT NOT NULL REFERENCES users(id),
    private BOOL,
    category INT REFERENCES goal_categories(id),
    goal_name VARCHAR(64),
    goal_body VARCHAR(4096),
    due_date TIMESTAMP,
    approval_count INT
);


CREATE TABLE  IF NOT EXISTS category_resources (
    id SERIAL PRIMARY KEY NOT NULL,
    category INT NOT NULL REFERENCES goal_categories(id),
    title VARCHAR(512),
    link VARCHAR(512),
    type INT NOT NULL
);

CREATE TABLE  IF NOT EXISTS charities (
    id SERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(64),
    image VARCHAR(512),
    link VARCHAR(512),
    raised_monthly INT DEFAULT 0,
    raised_total INT DEFAULT 0
);

-- CREATE TABLE payments (
--     id SERIAL PRIMARY KEY NOT NULL,
--     'user' INT NOT NULL REFERENCES users(id),
--     amount INT
-- );


SELECT * FROM goal_categories


CREATE TABLE approved_goals (
    user_id INT,
    goal_id INT,
    UNIQUE (user_id, goal_id)
);

-- resource type 0 = video, 1 = link

-- TODO: Insert images for charities and set starting values to 0
-- Populating categories table

INSERT INTO charities(name, link, image) VALUES ('Project Fit America', 'https://projectfitamerica.org', '/project_fit_america.png');

INSERT INTO charities(name, link, image) VALUES ('Childrens Scholarship Fund', 'https://scholarshipfund.org', '/childrens_scholarship_fund.png');

INSERT INTO charities(name, link, image) VALUES ('Feeding America', 'https://www.feedingamerica.org', '/feeding_america.png');

INSERT INTO charities(name, link, image) VALUES ('American Foundation for Suicide Prevention', 'https://afsp.org', '/american_foundation_for_suicide_prevention.png');

INSERT INTO charities(name, link, image) VALUES ('St. Jude Childrens Research Hospital', 'https://www.stjude.org', '/st_jude_childrens_research_hospital.png');

INSERT INTO charities(name, link, image) VALUES ('Childs Play', 'https://childsplaycharity.org', '/childs_play.png');

-- populating goal_categories
INSERT INTO goal_categories(name, description, color) VALUES ('Exercise', 'Physical Activities', 15569972);

INSERT INTO goal_categories(name, description, color) VALUES ('Academic', 'Studying, Homework, Grade Goals', 10227985);

INSERT INTO goal_categories(name, description, color) VALUES ('Mental Health', 'Setting aside time to refresh yourself, Journaling, Meditation', 6744214);

INSERT INTO goal_categories(name, description, color) VALUES ('Nutrition', 'Eating Healthy, Getting enough food, Portioning out food', 15303659);

INSERT INTO goal_categories(name, description, color) VALUES ('Chores', 'Walk the dog, Do dishes, Feed the fishes, etc.', 16774185);

INSERT INTO goal_categories(name, description, color) VALUES ('Hobbies', 'Write a book, Practice guitar, Practice skateboarding, etc.', 5088046);

INSERT INTO goal_categories(name, description, color) VALUES ('Work', 'Finish your presentation, Check your emails, Ask for a raise, etc.', 4986450);

INSERT INTO goal_categories(name, description, color) VALUES ('Sleep', 'Go to sleep, Wake up', 6833120);

-- populate resources

INSERT INTO category_resources(category, title, link, type) VALUES (1,'Highwayfit Exercise Video','https://www.youtube.com/watch?v=MMkZ7ZV5IXA', 0);

INSERT INTO category_resources(category, title, link, type) VALUES (1,'NY Times: How to Start Running','https://www.nytimes.com/guides/well/how-to-start-running', 1);

INSERT INTO category_resources(category, title, link, type) VALUES (1,'Fitness First: Beginners Guide to the Gym','https://www.fitnessfirst.co.uk/inside-track/fitness/the-ultimate-beginner-s-guide-to-the-gym/', 1);

INSERT INTO category_resources(category, title, link, type) VALUES (1,'Bicycling.com Beginner Tips','https://www.bicycling.com/skills-tips/a20003829/9-bike-beginner-mistakes-to-avoid/', 1);

INSERT INTO category_resources(category, title, link, type) VALUES (2,'Oxford Learning: How to Study Effectively','https://www.youtube.com/watch?v=MMkZ7ZV5IXA', 1);

INSERT INTO category_resources(category, title, link, type) VALUES (2,'CrashCourse Study Tips','https://www.youtube.com/watch?v=E7CwqNHn_Ns', 0);

INSERT INTO category_resources(category, title, link, type) VALUES (3,'BetterUp: How to Start Journaling','https://www.betterup.com/blog/how-to-start-journaling', 1);

INSERT INTO category_resources(category, title, link, type) VALUES (3,'UMich: Mental Health Tips','https://uhs.umich.edu/tenthings', 1);

INSERT INTO category_resources(category, title, link, type) VALUES (3,'HealthyGamerGG: Guided Sound Meditation','https://youtu.be/7VaHh9EUcfE', 0);

INSERT INTO category_resources(category, title, link, type) VALUES (3,'Headspace Stress-Relief Meditation','https://www.youtube.com/watch?v=sG7DBA-mgFY', 0);

INSERT INTO category_resources(category, title, link, type) VALUES (4,'US Nutrition Website','https://www.nutrition.gov', 1);

INSERT INTO category_resources(category, title, link, type) VALUES (4,'Noom','https://www.noom.com', 1);

INSERT INTO category_resources(category, title, link, type) VALUES (4,'MedicalNewsToday: Undereating','https://www.medicalnewstoday.com/articles/322157#how-to-deal-with-undereating', 1);

INSERT INTO category_resources(category, title, link, type) VALUES (5,'BHG: Bedroom Cleaning Checklist','https://www.bhg.com/homekeeping/house-cleaning/tips/quick-clean-bedroom/', 1);

INSERT INTO category_resources(category, title, link, type) VALUES (5,'The Spruce: How to Do Laundry','https://www.thespruce.com/how-to-do-laundry-2146149', 1);

INSERT INTO category_resources(category, title, link, type) VALUES (5,'BringFido: Dog Park Lookup by Location','https://www.bringfido.com/attraction/parks/', 1);

INSERT INTO category_resources(category, title, link, type) VALUES (6,'NY Times: Finding a Hobby','https://www.nytimes.com/guides/smarterliving/how-to-find-a-hobby', 1);

INSERT INTO category_resources(category, title, link, type) VALUES (6,'Blurb: How to Stick with a Hobby','https://www.blurb.com/blog/5-helpful-tips-pursue-hobbies-passions/', 1);

INSERT INTO category_resources(category, title, link, type) VALUES (6,'Develop Good Habits: Meet New People Through a Hobby','https://www.developgoodhabits.com/hobbies-meet-people/', 1);

INSERT INTO category_resources(category, title, link, type) VALUES (7,'Forbes: Increase Your Productivity at Work','https://www.forbes.com/sites/ashleystahl/2018/06/28/5-ways-to-increase-your-productivity-at-work/?sh=34c40a7a2138', 1);

INSERT INTO category_resources(category, title, link, type) VALUES (7,'BetterUp: How to be Assertive at Work','https://www.betterup.com/blog/assertiveness', 1);

INSERT INTO category_resources(category, title, link, type) VALUES (7,'Indeed: How to Ask for a Raise','https://www.indeed.com/career-advice/pay-salary/guide-how-to-ask-for-a-raise', 1);

INSERT INTO category_resources(category, title, link, type) VALUES (7,'WikiHow: How to Enjoy Your Job','https://www.wikihow.com/Enjoy-Your-Job', 1);

INSERT INTO category_resources(category, title, link, type) VALUES (8,'Headspace: Sleep Meditation','https://www.youtube.com/watch?v=ft-vhYwHzxw', 0);

INSERT INTO category_resources(category, title, link, type) VALUES (8,'Sleep Foundation: How to go to Sleep Earlier','https://www.sleepfoundation.org/sleep-hygiene/how-to-go-to-sleep-earlier', 1);

INSERT INTO category_resources(category, title, link, type) VALUES (8,'NPR: How to Wake Up Early','https://www.npr.org/2021/11/02/1051553451/how-to-wake-up-early', 1);

INSERT INTO category_resources(category, title, link, type) VALUES (8,'HealthLine: how to','https://www.healthline.com/health/healthy-sleep/how-to-fix-sleep-schedule', 1);
