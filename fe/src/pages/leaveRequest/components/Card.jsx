const Card = ({ name, quantity, color = "black", changeStatus }) => {
  return (
    <div
      className="stat-card"
      style={{ color: `${color}` }}
      onClick={() => changeStatus()}
    >
      <p>{name}</p>
      <p className="stat-value" style={{ color: `${color}` }}>
        {quantity}
      </p>
    </div>
  );
};

export default Card;
