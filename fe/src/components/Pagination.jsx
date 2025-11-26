import React from "react";
import { ChevronLeft, ChevronRight } from "lucide-react";

const Pagination = ({ pagination, currentPage, changePage, name }) => {
  const { totalPages, hasNext, hasPrevious, totalElements } = pagination;
  const current = currentPage;

  const pages = [];
  if (current > 2)
    pages.push(
      <button
        key={1}
        onClick={() => changePage(1)}
        className="pagination-number"
      >
        1
      </button>
    );
  if (current > 3) pages.push(<span key="start-dots">...</span>);
  for (
    let i = Math.max(1, current - 1);
    i <= Math.min(totalPages, current + 1);
    i++
  ) {
    pages.push(
      <button
        key={i}
        onClick={() => changePage(i)}
        className={`pagination-number ${current === i ? "active" : ""}`}
      >
        {i}
      </button>
    );
  }
  if (current < totalPages - 2) pages.push(<span key="end-dots">...</span>);
  if (current < totalPages - 1)
    pages.push(
      <button
        key={totalPages}
        onClick={() => changePage(totalPages)}
        className="pagination-number"
      >
        {totalPages}
      </button>
    );

  return (
    <div className="pagination-container">
      <div className="pagination-controls">
        <button
          onClick={() => changePage(current - 1)}
          disabled={!hasPrevious}
          className="pagination-btn"
        >
          <ChevronLeft size={20} />
        </button>
        <div className="pagination-numbers">{pages}</div>
        <button
          onClick={() => changePage(current + 1)}
          disabled={!hasNext}
          className="pagination-btn"
        >
          <ChevronRight size={20} />
        </button>
      </div>
      <div className="pagination-info">
        Tổng số: {totalElements} {name} 
      </div>
    </div>
  );
};

export default Pagination;
