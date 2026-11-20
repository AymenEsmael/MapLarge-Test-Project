-- -- 1
SELECT p.prom_id, p.prom_action, p.prom_period
FROM Promotions p
JOIN Flights_Promotions fp ON p.prom_id = fp.prom_id
WHERE fp.flight_id = 10;
-- -- 2
SELECT f.flight_id,pa.total_points flight_points, f.arrival_datetime
FROM FLIGHTS f
JOIN Point_Accounts pa on f.PASSID = pa.passid
JOIN Passengers p on f.PASSID = p.passid
WHERE p.pname = 'Aymen'; 
-- -- 3
SELECT fp.flight_id, COUNT(fp.prom_id) num_promotions
FROM Flights_Promotions fp
GROUP BY fp.flight_id
ORDER BY num_promotions DESC; 
-- -- 4
SELECT p.passid, p.pname passenger_name
FROM Passengers p
JOIN Flights f ON p.passid = f.passid
WHERE f.arrival_datetime BETWEEN TO_DATE('03/01/2023', 'MM/DD/YYYY') 
                            AND TO_DATE('03/15/2023', 'MM/DD/YYYY')
                            AND f.destination = 'Berlin';
-- -- 5
SELECT f.flight_id, f.source, f.destination,pa.total_points,ft.trip_id,f.arrival_datetime
FROM FLIGHTS f
JOIN FLIGHTS_TRIPS ft on ft.FLIGHT_ID = f.FLIGHT_ID 
JOIN Point_Accounts pa on pa.passid = f.passid
WHERE f.flight_id = 'FLT101';
-- --6
SELECT count(*) number_of_expired_cards 
from Cards
where expiry_date < SYSDATE;
-- -- 7
SELECT p.passid, p.pname
FROM Passengers p
JOIN Cards c ON p.passid = c.passid
WHERE c.expiry_date < SYSDATE
GROUP BY p.passid, p.pname
HAVING COUNT(c.card_id) = (
    SELECT MAX(expired_count)
    FROM (
        SELECT COUNT(c2.card_id) expired_count
        FROM Cards c2
        WHERE c2.expiry_date < SYSDATE
        GROUP BY c2.passid
    ) counts
)order by PASSID asc;
-- --8 quantity == number of points redeemed
SELECT rh.award_id, a.a_description, p.pname, rh.center_id, (a.points_required * rh.quantity) points_redeemed
from Redemption_History rh
JOIN Awards a on rh.award_id = a.award_id
Join Passengers p on rh.passid = p.passid
where pname = 'Mohammed';
-- --9
select p.pname, p.occupation
from PASSENGERS p
JOIN ADDRESSES a on p.passid = a.PASSID
where a.city = 'Fairfax';
-- --10
select sum(pa.total_points) total_points_of_Fairfax
from PASSENGERS p
JOIN ADDRESSES a on p.PASSID = a.PASSID
JOIN POINT_ACCOUNTS pa on pa.passid = p.passid
where a.city = 'Fairfax';
-- --11
select p.pname 
from PASSENGERS p
JOIN POINT_ACCOUNTS pa on p.passid = pa.PASSID
where pa.TOTAL_POINTS = 
(
    SELECT max(total_points)
    from POINT_ACCOUNTS
);

--12
select sum(a.POINTS_REQUIRED * rh.quantity) points_redeemed
from REDEMPTION_HISTORY rh
JOIN awards a on a.AWARD_ID = rh.AWARD_ID
where rh.REDEMPTION_DATE = TO_DATE('03/01/2023', 'MM/DD/YYYY');
--13 sum or count?
select count(rh.quantity)
from REDEMPTION_HISTORY rh
where rh.PASSID = 1;
-- --14
select count(rh.passid)
from REDEMPTION_HISTORY rh
where rh.center_id = 11;
-- --15
select count(*) total_awards
from awards;
-- --16
select p.pname
from passengers p
Join addresses a on p.passid = a.passid
where a.city = 'Fairfax' and p.occupation = 'Engineer';
-- --17
select *
from trips 
where trip_id Not IN (
    select trip_id
    from Flights_Trips
);
--18
select trip_id 
from (SELECT t.trip_id, COUNT(f.passid) AS passenger_count
            FROM trips t
            JOIN Flights_Trips ft ON ft.trip_id = t.trip_id
            JOIN Flights f ON f.flight_id = ft.flight_id
            GROUP BY t.trip_id
            ORDER BY t.trip_id ASC
)
where passenger_count = (
    select Max(passenger_count)
    from(
        SELECT t.trip_id, COUNT(f.passid) AS passenger_count
            FROM trips t
            JOIN Flights_Trips ft ON ft.trip_id = t.trip_id
            JOIN Flights f ON f.flight_id = ft.flight_id
            GROUP BY t.trip_id
            ORDER BY t.trip_id ASC
    )

);
