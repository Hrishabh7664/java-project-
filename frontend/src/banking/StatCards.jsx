import React from 'react';

const StatCards = ({ stats }) => {
  const formatCurrency = (val) => {
    if (val === null || val === undefined) return '$0.00';
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(val);
  };

  return (
    <section className="row g-4 mb-4" id="statsSection">
      <div className="col-12 col-sm-6 col-lg-4 col-xl-3">
        <div className="card shadow-sm border-0 h-100 p-4" style={{ borderRadius: '12px' }}>
          <div className="d-flex justify-content-between align-items-center mb-2">
            <span className="text-muted small fw-semibold text-uppercase">Total Balance</span>
            <span className="badge bg-primary-subtle text-primary p-2 rounded-3">
              <i className="bi bi-wallet2 fs-5"></i>
            </span>
          </div>
          <h3 className="fw-bold mb-1 text-dark">
            {formatCurrency(stats?.totalBalance)}
          </h3>
          <div className="small text-success mt-2">
            <i className="bi bi-arrow-up-short"></i> +4.2% vs last month
          </div>
        </div>
      </div>

      <div className="col-12 col-sm-6 col-lg-4 col-xl-3">
        <div className="card shadow-sm border-0 h-100 p-4" style={{ borderRadius: '12px' }}>
          <div className="d-flex justify-content-between align-items-center mb-2">
            <span className="text-muted small fw-semibold text-uppercase">Monthly Income</span>
            <span className="badge bg-success-subtle text-success p-2 rounded-3">
              <i className="bi bi-graph-up-arrow fs-5"></i>
            </span>
          </div>
          <h3 className="fw-bold mb-1 text-success">
            {formatCurrency(stats?.monthlyIncome)}
          </h3>
          <div className="small text-muted mt-2">
            <i className="bi bi-clock-history"></i> Real-time ledger
          </div>
        </div>
      </div>

      <div className="col-12 col-sm-6 col-lg-4 col-xl-3">
        <div className="card shadow-sm border-0 h-100 p-4" style={{ borderRadius: '12px' }}>
          <div className="d-flex justify-content-between align-items-center mb-2">
            <span className="text-muted small fw-semibold text-uppercase">Monthly Expenses</span>
            <span className="badge bg-danger-subtle text-danger p-2 rounded-3">
              <i className="bi bi-credit-card-2-back fs-5"></i>
            </span>
          </div>
          <h3 className="fw-bold mb-1 text-danger">
            {formatCurrency(stats?.monthlyExpenses)}
          </h3>
          <div className="small text-danger mt-2">
            <i className="bi bi-arrow-down-short"></i> Debits recorded
          </div>
        </div>
      </div>

      <div className="col-12 col-sm-6 col-lg-4 col-xl-3">
        <div className="card shadow-sm border-0 h-100 p-4" style={{ borderRadius: '12px' }}>
          <div className="d-flex justify-content-between align-items-center mb-2">
            <span className="text-muted small fw-semibold text-uppercase">Active Accounts</span>
            <span className="badge bg-info-subtle text-info p-2 rounded-3">
              <i className="bi bi-shield-check fs-5"></i>
            </span>
          </div>
          <h3 className="fw-bold mb-1 text-primary">
            {stats?.activeAccounts || 0}
          </h3>
          <div className="small text-muted mt-2">
            <i className="bi bi-patch-check"></i> Institutional Tier 1
          </div>
        </div>
      </div>
    </section>
  );
};

export default StatCards;
