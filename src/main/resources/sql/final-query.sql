SELECT
payments.amount AS SALARY,
CONCAT(employee.first_name, ' ', employee.last_name) AS NAME,
TIMESTAMPDIFF(YEAR, employee.dob, CURDATE()) AS AGE,
department.department_name AS DEPARTMENT_NAME
FROM payments
JOIN employee ON payments.emp_id = employee.emp_id
JOIN department ON employee.department = department.department_id
WHERE DAY(payments.payment_time) <> 1
AND payments.amount = (
SELECT MAX(amount)
FROM payments
WHERE DAY(payment_time) <> 1
);

