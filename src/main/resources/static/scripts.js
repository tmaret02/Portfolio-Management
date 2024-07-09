document.addEventListener('DOMContentLoaded', function() {
    const userForm = document.getElementById('userForm');
    const portfolioForm = document.getElementById('portfolioForm');
    const clearPortfolioForm = document.getElementById('clearPortfolioForm');
    const fetchPortfoliosBtn = document.getElementById('fetchPortfoliosBtn');
    const portfoliosDiv = document.getElementById('portfolios');
    const balanceForm = document.getElementById('balanceForm');
    const balanceDiv = document.getElementById('balance');

    userForm.addEventListener('submit', function(event) {
        event.preventDefault();
        const username = document.getElementById('username').value;
        const initialBalance = parseFloat(document.getElementById('initialBalance').value); // Convert to float
        
        console.log(`Submitting Username: ${username}, Initial Balance: ${initialBalance}`);

        fetch('/api/stocks/user', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, initialBalance }),
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text); });
            }
            return response.text();
        })
        .then(data => alert(data))
        .catch(error => alert('Error: ' + error.message));
    });

    portfolioForm.addEventListener('submit', function(event) {
        event.preventDefault();
        const username = document.getElementById('portfolioUsername').value;
        const stockSymbol = document.getElementById('stockSymbol').value.toUpperCase();
        const quantity = parseInt(document.getElementById('quantity').value);

        fetch(`/api/stocks/${username}/portfolio`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({ stockId: stockSymbol, quantity: quantity })
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text); });
            }
            return response.text();
        })
        .then(data => alert(data))
        .catch(error => alert('Error: ' + error.message));
    });

    clearPortfolioForm.addEventListener('submit', function(event) {
        event.preventDefault();
        const username = document.getElementById('clearUsername').value;

        fetch(`/api/stocks/${username}/portfolio`, {
            method: 'DELETE'
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text); });
            }
            return response.text();
        })
        .then(data => alert(data))
        .catch(error => alert('Error: ' + error.message));
    });

    fetchPortfoliosBtn.addEventListener('click', function() {
        const username = document.getElementById('fetchUsername').value;

        fetch(`/api/stocks/${username}/portfolio`)
        .then(response => response.json())
        .then(data => {
            portfoliosDiv.innerHTML = '';
            data.forEach(portfolio => {
                const portfolioDiv = document.createElement('div');
                portfolioDiv.textContent = `User ID: ${portfolio.userId}, Stock ID: ${portfolio.stockId}, Quantity: ${portfolio.quantity}, Last Price: ${portfolio.lastPrice}`;
                portfoliosDiv.appendChild(portfolioDiv);
            });
        })
        .catch(error => console.error('Error:', error));
    });

    balanceForm.addEventListener('submit', function(event) {
        event.preventDefault();
        const username = document.getElementById('balanceUsername').value;

        fetch(`/api/stocks/${username}/balance`)
        .then(response => response.json())
        .then(data => {
            balanceDiv.innerHTML = `Initial Balance: ${data.initialBalance}, Remaining Balance: ${data.remainingBalance}`;
        })
        .catch(error => console.error('Error:', error));
    });
});

