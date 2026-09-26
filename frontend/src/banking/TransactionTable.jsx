import React from 'react';

const TransactionTable = ({ transactions }) => {
  const formatCurrency = (amount, type) => {
    const formatted = new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(amount);
    return type === 'DEBIT' ? `-${formatted}` : `+${formatted}`;
  };

  const formatDate = (isoString) => {
    if (!isoString) return 'Just now';
    try {
      const date = new Date(isoString);
      return date.toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch {
      return isoString;
    }
  };

  return (
    <div className="card shadow-sm border-0 h-100 p-4" style={{ borderRadius: '12px' }}>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h5 className="fw-bold mb-0 text-dark">
          <i className="bi bi-list-columns-reverse text-primary me-2"></i>
          Recent Transactions
        </h5>
        <span className="badge bg-secondary-subtle text-secondary px-3 py-2 rounded-pill">
          {transactions ? transactions.length : 0} Records
        </span>
      </div>
      <p className="text-muted small mb-3">Ledger transactions retrieved via Spring Boot REST service.</p>

      <div className="table-responsive">
        <table className="table table-hover align-middle mb-0">
          <thead className="table-light">
            <tr>
              <th scope="col" className="small text-uppercase text-muted">Tx ID</th>
              <th scope="col" className="small text-uppercase text-muted">Description</th>
              <th scope="col" className="small text-uppercase text-muted">Type</th>
              <th scope="col" className="small text-uppercase text-muted">Amount</th>
              <th scope="col" className="small text-uppercase text-muted">Date</th>
              <th scope="col" className="small text-uppercase text-muted">Status</th>
            </tr>
          </thead>
          <tbody>
            {transactions && transactions.length > 0 ? (
              transactions.map((tx) => (
                <tr key={tx.id || tx.transactionId}>
                  <td className="fw-semibold small text-primary">{tx.transactionId}</td>
                  <td>
                    <div className="fw-medium text-dark">{tx.description}</div>
                    <small className="text-muted">{tx.holderName || tx.accountNumber}</small>
                  </td>
                  <td>
                    <span className={`badge ${tx.type === 'CREDIT' ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'}`}>
                      {tx.type}
                    </span>
                  </td>
                  <td className={`fw-bold ${tx.type === 'CREDIT' ? 'text-success' : 'text-danger'}`}>
                    {formatCurrency(tx.amount, tx.type)}
                  </td>
                  <td className="small text-muted">{formatDate(tx.timestamp)}</td>
                  <td>
                    <span className="badge bg-success">
                      {tx.status || 'COMPLETED'}
                    </span>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="6" className="text-center py-4 text-muted">
                  No transactions recorded yet.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default TransactionTable;
