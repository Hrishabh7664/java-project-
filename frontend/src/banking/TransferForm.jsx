import React, { useState } from 'react';

const TransferForm = ({ accounts, onSubmitTransfer, isSubmitting }) => {
  const [fromAccountNumber, setFromAccountNumber] = useState(
    accounts && accounts.length > 0 ? accounts[0].accountNumber : ''
  );
  const [toAccount, setToAccount] = useState('');
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');
  const [validationError, setValidationError] = useState('');

  // Keep fromAccountNumber synchronized when accounts load
  React.useEffect(() => {
    if (!fromAccountNumber && accounts && accounts.length > 0) {
      setFromAccountNumber(accounts[0].accountNumber);
    }
  }, [accounts, fromAccountNumber]);

  const handleSubmit = (e) => {
    e.preventDefault(); // Experiment 2 & 3 mandatory requirement
    setValidationError('');

    const parsedAmount = parseFloat(amount);
    if (!fromAccountNumber) {
      setValidationError('Please select a source account.');
      return;
    }
    if (!toAccount.trim()) {
      setValidationError('Please specify a beneficiary account.');
      return;
    }
    if (isNaN(parsedAmount) || parsedAmount <= 0) {
      setValidationError('Please enter a valid transfer amount greater than $0.00.');
      return;
    }
    if (!description.trim()) {
      setValidationError('Please provide a payment note/description.');
      return;
    }

    const payload = {
      fromAccountNumber,
      toAccount: toAccount.trim(),
      amount: parsedAmount,
      description: description.trim()
    };

    onSubmitTransfer(payload, () => {
      // Form reset callback on success
      setToAccount('');
      setAmount('');
      setDescription('');
    });
  };

  return (
    <div className="card shadow-sm border-0 h-100 p-4" style={{ borderRadius: '12px' }}>
      <div className="d-flex align-items-center mb-3">
        <span className="badge bg-primary-subtle text-primary p-2 rounded-3 me-2">
          <i className="bi bi-send fs-5"></i>
        </span>
        <h5 className="fw-bold mb-0 text-dark">Instant Funds Transfer</h5>
      </div>
      <p className="text-muted small mb-3">Controlled form communicating directly with Spring Boot REST endpoint.</p>

      {validationError && (
        <div className="alert alert-danger py-2 small mb-3" role="alert">
          <i className="bi bi-exclamation-triangle-fill me-1"></i> {validationError}
        </div>
      )}

      <form onSubmit={handleSubmit} noValidate>
        <div className="mb-3">
          <label htmlFor="sourceSelect" className="form-label small fw-semibold text-secondary">
            Source Account
          </label>
          <select
            id="sourceSelect"
            className="form-select"
            value={fromAccountNumber}
            onChange={(e) => setFromAccountNumber(e.target.value)}
            disabled={isSubmitting}
            required
          >
            {accounts && accounts.length > 0 ? (
              accounts.map((acc) => (
                <option key={acc.id} value={acc.accountNumber}>
                  {acc.accountNumber} - {acc.holderName} (${acc.balance?.toFixed(2)})
                </option>
              ))
            ) : (
              <option value="">No Accounts Available</option>
            )}
          </select>
        </div>

        <div className="mb-3">
          <label htmlFor="beneficiaryInput" className="form-label small fw-semibold text-secondary">
            Beneficiary Account
          </label>
          <div className="input-group">
            <span className="input-group-text bg-light"><i className="bi bi-person"></i></span>
            <input
              id="beneficiaryInput"
              type="text"
              className="form-control"
              placeholder="e.g. AC100002"
              value={toAccount}
              onChange={(e) => setToAccount(e.target.value)}
              disabled={isSubmitting}
              required
            />
          </div>
        </div>

        <div className="mb-3">
          <label htmlFor="amountInput" className="form-label small fw-semibold text-secondary">
            Amount ($ USD)
          </label>
          <div className="input-group">
            <span className="input-group-text bg-light">$</span>
            <input
              id="amountInput"
              type="number"
              step="0.01"
              min="0.01"
              className="form-control"
              placeholder="0.00"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              disabled={isSubmitting}
              required
            />
          </div>
        </div>

        <div className="mb-4">
          <label htmlFor="noteInput" className="form-label small fw-semibold text-secondary">
            Payment Note / Reference
          </label>
          <input
            id="noteInput"
            type="text"
            className="form-control"
            placeholder="e.g. Project supplies, Book purchase"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            disabled={isSubmitting}
            required
          />
        </div>

        <button
          type="submit"
          className="btn btn-primary w-100 py-2 rounded-pill fw-semibold shadow-sm"
          disabled={isSubmitting}
        >
          {isSubmitting ? (
            <>
              <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              Authorizing Transfer...
            </>
          ) : (
            <>
              <i className="bi bi-arrow-right-circle me-1"></i> Authorize & Send Transfer
            </>
          )}
        </button>
      </form>
    </div>
  );
};

export default TransferForm;
